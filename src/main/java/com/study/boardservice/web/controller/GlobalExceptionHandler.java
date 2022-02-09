package com.study.boardservice.web.controller;

import com.study.boardservice.exception.*;
import com.study.boardservice.web.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MissingEssentialValueException.class)
    public ResponseEntity<?> handleMissingEssentialValueException(MissingEssentialValueException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = InvalidValueException.class)
    public ResponseEntity<?> handleInvalidValidException(InvalidValueException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = DuplicateValueException.class)
    public ResponseEntity<?> handleDuplicateValueException(DuplicateValueException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = UnexpectedAccessException.class)
    public ResponseEntity<?> handleUnexpectedAccessException(UnexpectedAccessException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = NotFoundValueException.class)
    public ResponseEntity<?> handleNotFoundValueException(NotFoundValueException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
}
