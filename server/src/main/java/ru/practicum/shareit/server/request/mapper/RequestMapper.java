package ru.practicum.shareit.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.model.Request;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ItemMapper.class})
public interface RequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "createdAt", target = "created")
    @Mapping(source = "items", target = "items")
    RequestDto toDto(Request request);
}
