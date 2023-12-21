package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.utils.ValidatorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.dto.CommentMapper.COMMENT_MAPPER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ValidatorService validatorService;

    @Override
    @Transactional
    public void deleteAdmin(Long comId) {
        Comment comment = validatorService.existCommentById(comId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        validText(newCommentDto.getText());
        User user = validatorService.existUserById(userId);
        validatorService.existEventById(eventId);
        Event event = validatorService.existEventById(eventId);
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return COMMENT_MAPPER.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(Long userId, Long comId, NewCommentDto newCommentDto) {
        validText(newCommentDto.getText());
        User user = validatorService.existUserById(userId);
        Comment comment = validatorService.existCommentById(comId);
        if (!comment.getAuthor().equals(user)) {
            throw new ValidationException("Изменение невозможно, пользователь с ID- " + userId + " не является автором" +
                    " комментария.");
        }
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return COMMENT_MAPPER.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getComment(Long userId, Long comId) {
        validatorService.existUserById(userId);
        Comment comment = validatorService.existCommentById(comId);
        return COMMENT_MAPPER.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentList(Long eventId) {
        List<Comment> commentList = commentRepository.findAllByEventId(eventId);
        return commentList.stream()
                .map(COMMENT_MAPPER::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getUserComments(Long userId) {
        validatorService.existUserById(userId);
        List<Comment> commentList = commentRepository.findAllByAuthorId(userId);
        if (commentList.isEmpty()) {
            return new ArrayList<>();
        }
        return commentList.stream()
                .map(COMMENT_MAPPER::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserComment(Long userId, Long comId) {
        Comment comment = validatorService.existCommentById(comId);
        User user = validatorService.existUserById(userId);
        if (!comment.getAuthor().equals(user)) {
            throw new ValidationException("Пользователь с ID-" + userId + "не является автором сомментария с ID-" + comId
                    + ". Удаление комментария невозможно.");
        }
        commentRepository.delete(comment);
    }

    private void validText(String text) {
        if (StringUtils.isBlank(text)) {
            throw new ValidationException("Нельзя отправить пустой комментарий.");
        }
        if (text.length() > 2000) {
            throw new ValidationException("Количество символов в комментарии не может быть длинее 2000 символов.");
        }
    }
}
