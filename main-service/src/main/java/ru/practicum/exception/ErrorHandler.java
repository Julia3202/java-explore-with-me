package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final OtherException e) {
        log.error("Error 500: {}", "Произошла внутренняя ошибка сервера.");
        String reason = "Произошла внутренняя ошибка сервера.";
        return ApiError.builder()
                .error(e.getStackTrace())
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        String reason = "Искомый объект не найден.";
        return ApiError.builder()
                .error(e.getStackTrace())
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final ValidationException e) {
        String reason = "Некорректный запрос.";
        return ApiError.builder()
                .error(e.getStackTrace())
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        String reason = "Запрос кофликтует с текущим состоянием сервера.";
        return ApiError.builder()
                .error(e.getStackTrace())
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }
}
