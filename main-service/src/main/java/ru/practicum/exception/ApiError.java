package ru.practicum.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String error;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
