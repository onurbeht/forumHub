package com.forumHub.infra.exceptions;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException(String msg) {
        super(msg);
    }

}
