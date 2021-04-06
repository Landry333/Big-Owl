package com.example.bigowlapp.repository.exception;

import com.google.i18n.phonenumbers.NumberParseException;

public class EmptyFieldException extends NumberParseException {
    private static final String DEFAULT_MESSAGE = "This field is empty. Please fill in an appropriate value.";

    public EmptyFieldException() {
        super(ErrorType.NOT_A_NUMBER, DEFAULT_MESSAGE);
    }

    public EmptyFieldException(String message) {
        super(ErrorType.NOT_A_NUMBER, message);
    }
}
