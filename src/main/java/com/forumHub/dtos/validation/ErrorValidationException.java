package com.forumHub.dtos.validation;

public record ErrorValidationException(
        String field,
        String message) {

}
