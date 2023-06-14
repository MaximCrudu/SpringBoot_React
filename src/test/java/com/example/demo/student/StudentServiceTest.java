package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // will manage the mock actions like: initialize mock and closing mock resource
class StudentServiceTest {

    /**
     * Mock because we won't test real StudentRepository when we want to test StudentService
     * Benefit, fast unit tests because don't have to:
     * - bring database
     * - create a table
     * - insert new data
     * - drop the database
     * - etc
     * */
    @Mock
    private StudentRepository studentRepository;

    private StudentService underTest;

    // Will run before each test
    @BeforeEach
    void setUp(){
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        // the service method is called
        underTest.getAllStudents();

        // then
        // verify if the repository was invoked using the findAll() method.
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student(
                "Johnny",
                "johnny@mail.com",
                Gender.MALE
        );

        // when
        underTest.addStudent(student);

        // then
        // prepare argument capture
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        // verify if the repository was invoked using the save() method
        // and capture the value of the transmitted Student object
        verify(studentRepository)
                .save(studentArgumentCaptor.capture());

        Student captureStudent = studentArgumentCaptor.getValue();

        // check if studentRepository was invoked with the same object that was passed in StudentService underTest
        assertThat(captureStudent).isEqualTo(student);
    }

    @Test
    void shouldThrowWhenEmailIsTaken() {
        // given
        Student student = new Student(
                "Johnny",
                "johnny@mail.com",
                Gender.MALE
        );
        // set mock studentRepository to return true from calls on selectExistsEmail method
        given(studentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " is taken");

        // to ensure that nothing will be saved by the mock after throwing an error
        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        long id = 2;
        // set mock studentRepository to return true from calls on existsById method
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        // verify if the studentRepository was invoked using the deleteById() method.
        verify(studentRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteStudentNotFound() {
        // given
        long id = 3;
        given(studentRepository.existsById(id))
                .willReturn(false);
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        // to ensure that nothing will be deleted by the mock after throwing an error
        verify(studentRepository, never()).deleteById(any());
    }
}