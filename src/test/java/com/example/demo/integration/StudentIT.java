package com.example.demo.integration;

import com.example.demo.student.Gender;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * For the integration tests we are using a persistent Postgres database as a local docker container database
 * */

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    // We could test a controller by performing http requests against
    // this controller to invoke actually the mapping annotations (ex. @PostMapping)
    @Autowired
    private MockMvc mockMvc;

    // To pass the object in the mapping format
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    StudentRepository studentRepository;

    // to avoid exceptions, generate random data in case for multiple integration tests
    private final Faker faker = new Faker();

    private Student createStudent() {
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );
        String email = String.format(
                "%s@%s",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()),
                faker.internet().domainName()
        );
        Gender gender = Gender.values()[new Random().nextInt(Gender.values().length)];
        return new Student(
                name,
                email,
                gender
        );
    }

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
        Student student = createStudent();

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)));
        // then
        resultActions.andExpect(status().isOk());
        // Check that the student is entered in the database
        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingElementComparatorIgnoringFields("id")
                .contains(student);
    }

    @Test
    void canRegister10NewStudents() throws Exception {
        for(int i = 0; i < 10; i++) {
            // given
            Student student = createStudent();

            // when
            ResultActions resultActions = mockMvc
                    .perform(post("/api/v1/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(student)));
            // then
            resultActions.andExpect(status().isOk());
            // Check that the student is entered in the database
            List<Student> students = studentRepository.findAll();
            assertThat(students)
                    .usingElementComparatorIgnoringFields("id")
                    .contains(student);
        }
    }

    @Test
    void canDeleteStudent() throws Exception {
        // given
        int totalStudents = (int) studentRepository.count();

        // when
        if (totalStudents > 0) {
            List<Student> students = studentRepository.findAll();
            // will delete last added student in database
            Student student = students.get(totalStudents - 1);
            long id = student.getId();
            ResultActions resultActions = mockMvc
                    .perform(delete("/api/v1/students/" + id));

            // then
            resultActions.andExpect(status().isOk());
            boolean exists = studentRepository.existsById(id);
            assertThat(exists).isFalse();
        }
    }

    @Test
    void canAddThenDeleteStudent() throws Exception {
        // given
        Student student = createStudent();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        // retrieve result data
        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        // map the result string contentAsString into an array of objects
        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + student.getEmail() + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    void canUpdateExistingStudent() throws Exception {
        // given
        Student student = createStudent();
        Student newDataForStudent = createStudent();
        System.out.println("student.getId() " + student.getId());

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        // retrieve result data
        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        // map the result string contentAsString into an array of objects
        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        // extract assigned id of added student
        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + student.getEmail() + " not found"));

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/v1/students/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDataForStudent)));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isTrue();

        // Retrieve the updated student from the repository
        Optional<Student> updatedStudentOptional = studentRepository.findById(id);
        assertThat(updatedStudentOptional).isPresent();
        Student updatedStudent = updatedStudentOptional.get();

        // Compare the fields of newDataForStudent and updatedStudent from database
        assertThat(updatedStudent.getName()).isEqualTo(newDataForStudent.getName());
        assertThat(updatedStudent.getEmail()).isEqualTo(newDataForStudent.getEmail());
        assertThat(updatedStudent.getGender()).isEqualTo(newDataForStudent.getGender());
    }
}
