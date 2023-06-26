package com.example.demo.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{

    private final int statusCode;

    public BadRequestException(String msg) {
        super(msg);
        this.statusCode = HttpStatus.BAD_REQUEST.value();
    }

    public BadRequestException(BindingResult msg) {
        super(setErrMsg(msg));
        this.statusCode = HttpStatus.BAD_REQUEST.value();
    }

    public int getStatusCode() {
        return statusCode;
    }

    private static String setErrMsg(BindingResult msg) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : msg.getFieldErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("\n");
        }
        return errorMessage.toString();
    }
}
