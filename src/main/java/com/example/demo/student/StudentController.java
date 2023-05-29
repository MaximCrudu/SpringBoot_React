package com.example.demo.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

// Gives ability to expose resources, so endpoints that the clients can consume
@RestController
// Changing the url
@RequestMapping(path = "api/v1/students")
public class StudentController {

    /**
     * to expose the return of this function, we should to expose this as
     * an endpoint, so annotate it
     */
    @GetMapping
    public List<Student> getAllStudents() {
        List<Student> students = Arrays.asList(
                new Student(
                        1L,
                        "Jamila",
                        "jamila@amigoscode.edu",
                        Gender.FEMALE),
                new Student(
                        2L,
                        "Alex",
                        "alex@amigoscode.edu",
                        Gender.MALE)
        );
        return students;
    }
}
