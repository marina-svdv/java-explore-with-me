package ru.practicum.ewm.main.exception;

public class InvalidEventDateException extends RuntimeException {

    public InvalidEventDateException(String message) {
        super(message);
    }
}