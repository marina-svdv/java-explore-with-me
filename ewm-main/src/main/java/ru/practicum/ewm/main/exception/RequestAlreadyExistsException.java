package ru.practicum.ewm.main.exception;

public class RequestAlreadyExistsException extends RuntimeException {

    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}