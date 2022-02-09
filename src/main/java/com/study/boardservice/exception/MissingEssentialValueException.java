package com.study.boardservice.exception;

import lombok.Getter;

@Getter
public class MissingEssentialValueException extends RuntimeException{
    private String message;

    public MissingEssentialValueException(String message){
        this.message = message;
    }
}
