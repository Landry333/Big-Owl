package com.example.bigowlapp.repository.exception;

public class PhoneNumberTakenException extends Exception {

    private static final String DEFAULT_MESSAGE = "This phone number is already associated to an account";

    public PhoneNumberTakenException() {
        super(DEFAULT_MESSAGE);
    }

    public PhoneNumberTakenException(String message) {
        super(message);
    }
}
