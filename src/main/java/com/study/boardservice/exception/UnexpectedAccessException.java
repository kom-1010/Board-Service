package com.study.boardservice.exception;

import lombok.Getter;

@Getter
public class UnexpectedAccessException extends RuntimeException {
    private String message;

    public UnexpectedAccessException(String message) {
        this.message = message;
    }
}
