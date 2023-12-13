package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final OtherException e) {
        String reason = "Произошла внутренняя ошибка сервера.";
        return ApiError.builder()
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
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBindException(final BindException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message("Field: " + e.getFieldError().getField() +
                        ". Error: " + e.getFieldError().getDefaultMessage() +
                        ". Value: " + e.getFieldError().getRejectedValue())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBindException(final MissingServletRequestParameterException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        String reason = "Запрос кофликтует с текущим состоянием сервера.";
        return ApiError.builder()
                .message(e.getMessage())
                .reason(reason)
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }
}
