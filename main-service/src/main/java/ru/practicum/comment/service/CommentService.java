package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    void deleteAdmin(Long comId);

    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto update(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto getComment(Long userId, Long comId);

    List<CommentDto> getCommentList(Long eventId);

    List<CommentDto> getUserComments(Long userId);

    void deleteUserComment(Long userId, Long comId);
}
