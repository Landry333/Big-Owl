package com.example.bigowlapp.repository.exception;

public class PhoneNumberTakenException extends Exception {

    private static final String DEFAULT_MESSAGE = "Phone Number is already in the database.";

    public PhoneNumberTakenException() {
        super(DEFAULT_MESSAGE);
    }

    public PhoneNumberTakenException(String message) {
        super(message);
    }
}
