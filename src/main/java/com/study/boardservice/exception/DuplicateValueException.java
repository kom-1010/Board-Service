package com.study.boardservice.exception;

import lombok.Getter;

@Getter
public class DuplicateValueException extends RuntimeException {
    private String message;

    public DuplicateValueException(String message) {
        this.message = message;
    }
}
