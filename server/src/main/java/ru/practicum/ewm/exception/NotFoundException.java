package ru.practicum.ewm.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String mes) {
        super(mes);
    }
}
