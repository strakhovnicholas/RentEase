package ru.practicum.shareit.server.item.exceptions;


import ru.practicum.shareit.server.exception.common.AccessDeniedException;

public class ItemAccessDeniedException extends AccessDeniedException {
    public ItemAccessDeniedException(String message) {
        super(message);
    }
}
