package ru.practicum.ewm.main.exception;

public class InvalidEventStateException extends RuntimeException {

    public InvalidEventStateException(String message) {
        super(message);
    }
}