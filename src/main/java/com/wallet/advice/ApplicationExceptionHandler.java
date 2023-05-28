package com.wallet.advice;

import com.wallet.exception.PartnerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<?> handleInvalidParameterException(InvalidParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PartnerException.class)
    public ResponseEntity<?> handlePartnerException(PartnerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getPartnerErrorDTO());
    }

}
