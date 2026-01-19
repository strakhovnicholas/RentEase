package ru.practicum.shareit.server;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = {
        "ru.practicum.shareit.server.booking.mapper",
        "ru.practicum.shareit.server.item.mapper",
        "ru.practicum.shareit.server.user.mapper",
        "ru.practicum.shareit.server.request.mapper",
        "ru.practicum.shareit.server.comment.mapper"
})
public class AllMappersTestConfig {
}