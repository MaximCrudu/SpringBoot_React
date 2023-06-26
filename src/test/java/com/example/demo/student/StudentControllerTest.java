package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private BindingResult bindingResult;

    private StudentController studentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        studentController = new StudentController(studentService);
    }

    @Test
    public void testAddStudentWithBindingErrors() {
        // given
        // Mock a student with binding errors
        Student student = new Student();

        // when
        when(bindingResult.hasErrors()).thenReturn(true);

        // then
        // Assert that a BadRequestException is thrown
        try {
            studentController.addStudent(student, bindingResult);
        } catch (BadRequestException e) {
            int statusCode = e.getStatusCode();
            // Perform assertions on the status code (Bad request)
            assertThat(statusCode).isEqualTo(400);
        }

        // Verify that the student service is not called
        verify(studentService, never()).addStudent(any(Student.class));
    }

    @Test
    public void testUpdateStudentWithBindingErrors() {
        // given
        // Mock a student with binding errors
        Student student = new Student(
                "George",
                "geo@meo.com",
                Gender.MALE
        );
        long id = 5;

        // when
        when(bindingResult.hasErrors()).thenReturn(true);

        // then
        // Assert that a BadRequestException is thrown
        try {
            studentController.updateStudent(id, student, bindingResult);
        } catch (BadRequestException e) {
            int statusCode = e.getStatusCode();
            // Perform assertions on the status code (Bad request)
            assertThat(statusCode).isEqualTo(400);
        }

        // Verify that the student service is not called
        verify(studentService, never()).updateStudent(eq(id), any(Student.class));
    }
}