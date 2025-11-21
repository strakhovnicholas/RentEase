package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", defaultValue = "false")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item toEntity(ItemRequestDto dto);

    @Mapping(source = "owner.id", target = "ownerId")
    ItemResponseDto toDto(Item entity);

    void updateItemFromDto(ItemUpdateDto dto, @MappingTarget Item entity);
}


