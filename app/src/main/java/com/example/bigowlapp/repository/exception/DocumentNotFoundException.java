package com.example.bigowlapp.repository.exception;

public class DocumentNotFoundException extends Exception {

    private static final String DEFAULT_MESSAGE = "Document not found.";

    public DocumentNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
