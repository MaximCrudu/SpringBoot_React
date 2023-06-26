package com.example.demo.student;

import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.example.demo.student.exception.BadRequestException;


import javax.validation.Valid;
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

    @PostMapping
    public void addStudent(@Valid @RequestBody Student student, BindingResult bindingResult) {
        validateAndThrowBadRequest(bindingResult);

        studentService.addStudent(student);
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(
            @PathVariable("studentId") Long studentId) {

        studentService.deleteStudent(studentId);
    }

    @PutMapping(path = "{studentId}")
    public void updateStudent(
            @PathVariable("studentId") Long studentId,
            @Valid @RequestBody Student student, BindingResult bindingResult) {
        validateAndThrowBadRequest(bindingResult);

        studentService.updateStudent(studentId, student);
    }

    private void validateAndThrowBadRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            }
            throw new BadRequestException(errorMessage.toString());
        }
    }
}
