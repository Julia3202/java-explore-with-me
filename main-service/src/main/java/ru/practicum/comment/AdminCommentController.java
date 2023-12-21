package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.CommentService;

@RestController
@RequestMapping("/admin/comments/{comId}")
@Slf4j
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete(@PathVariable Long comId) {
        log.info("Поступил запрос администратора на удаление комментария с ID- {}", comId);
        commentService.deleteAdmin(comId);
    }
}
