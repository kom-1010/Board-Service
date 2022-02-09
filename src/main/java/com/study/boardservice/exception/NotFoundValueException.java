package com.study.boardservice.exception;

import lombok.Getter;

@Getter
public class NotFoundValueException extends RuntimeException {
    private String message;

    public NotFoundValueException(String message) {
        this.message = message;
    }
}
