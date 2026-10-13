package com.klu.jobtracker.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException() {
        super("Email already registered. Sign in or use a different email address.");
    }
}
