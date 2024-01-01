package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DuplicateVendorException extends RuntimeException {
    public DuplicateVendorException(String message) {
        super(message);
    }

//    public DuplicateVendorException(String message, Throwable cause) {
//        super(message, cause);
//    }
}
