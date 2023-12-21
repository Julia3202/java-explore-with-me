package ru.practicum.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.comment.model.Comment;

@Mapper
public interface CommentMapper {
    CommentMapper COMMENT_MAPPER = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);
}
