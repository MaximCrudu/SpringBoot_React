package com.example.demo.student;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

// Gives ability to expose resources, so endpoints that the clients can consume
@RestController
// Changing the url
@RequestMapping(path = "api/v1/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;
    /**
     * to expose the return of this function, we should to expose this as
     * an endpoint, so annotate it
     */
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
}
