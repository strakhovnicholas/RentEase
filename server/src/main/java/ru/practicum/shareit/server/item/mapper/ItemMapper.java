package ru.practicum.shareit.server.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.server.item.dto.CreateItemDto;
import ru.practicum.shareit.server.item.dto.ItemResponseDto;
import ru.practicum.shareit.server.item.dto.ItemUpdateDto;
import ru.practicum.shareit.server.item.model.Item;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    Item toEntity(CreateItemDto dto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    ItemResponseDto toDto(Item entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemUpdateDto dto, @MappingTarget Item entity);
}