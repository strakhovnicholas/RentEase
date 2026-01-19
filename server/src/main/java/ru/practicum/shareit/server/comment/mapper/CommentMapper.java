package ru.practicum.shareit.server.comment.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "itemId", source = "item.id")
    CommentDto toDto(Comment comment);
}
