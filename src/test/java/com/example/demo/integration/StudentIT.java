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
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
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
        Student student = new Student(
                name,
                email,
                gender
        );
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
        Student student = new Student(
                name,
                email,
                gender
        );

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

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
                                "student with email: " + email + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }
}
