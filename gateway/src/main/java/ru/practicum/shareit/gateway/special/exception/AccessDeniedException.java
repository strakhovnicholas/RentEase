package ru.practicum.shareit.gateway.special.exception;

public abstract class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
