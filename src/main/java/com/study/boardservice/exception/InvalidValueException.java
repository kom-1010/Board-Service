package com.study.boardservice.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends RuntimeException {
    private String message;

    public InvalidValueException(String message) {
        this.message = message;
    }
}
