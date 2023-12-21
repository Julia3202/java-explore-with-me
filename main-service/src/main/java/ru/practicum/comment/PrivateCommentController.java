package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long userId, @PathVariable Long eventId,
                             @RequestBody NewCommentDto newCommentDto) {
        log.info("Поступил запрос на создание комментария от пользователя с ID- {} для события с ID- {}.", userId,
                eventId);
        return commentService.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{comId}")
    public CommentDto update(@PathVariable Long userId, @PathVariable Long comId,
                             @RequestBody NewCommentDto newCommentDto) {
        log.info("Поступил запрос на изменение комментария с ID- {} от пользователя с ID- {}.", comId, userId);
        return commentService.update(userId, comId, newCommentDto);
    }

    @GetMapping("/{comId}")
    public CommentDto getComment(@PathVariable Long userId, @PathVariable Long comId) {
        log.info("Поступил запрос от пользователя с ID- {} на получение комментария с ID- {}.", userId, comId);
        return commentService.getComment(userId, comId);
    }

    @GetMapping
    public List<CommentDto> getUserComment(@PathVariable Long userId) {
        log.info("Поступил запрос на получение всех комментариев пользователя с ID- {}.", userId);
        return commentService.getUserComments(userId);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserComment(@PathVariable Long userId, @PathVariable Long comId) {
        log.info("Поступил запрос от пользователя с ID- {} на удаление своего комментария с ID- {}", userId, comId);
        commentService.deleteUserComment(userId, comId);
    }
}
