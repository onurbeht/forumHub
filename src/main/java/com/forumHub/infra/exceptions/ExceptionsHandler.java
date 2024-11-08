package com.forumHub.infra.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<String> handleWrongPasswordException(WrongPasswordException msg) {
        return ResponseEntity.badRequest().body(msg.getMessage());
    }

}
