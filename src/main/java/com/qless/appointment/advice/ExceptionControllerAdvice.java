package com.qless.appointment.advice;

import com.qless.appointment.exception.CSVFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({CSVFormatException.class, IOException.class})
    public ErrorResponse csvFormatException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("message", e.getMessage());
        return ErrorResponse.builder(e, problemDetail).build();
    }
}
