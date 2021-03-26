package com.example.bigowlapp.repository.exception;

public class EmptyFieldException extends Exception{
    private static final String DEFAULT_MESSAGE = "This field is empty. Please fill in an appropriate value.";

    public EmptyFieldException() {
        super(DEFAULT_MESSAGE);
    }

    public EmptyFieldException(String message) {
        super(message);
    }
}
