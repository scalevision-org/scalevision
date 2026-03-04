package com.scalevision.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private String code;
    private Integer status;
    private String message;
    private String userMessage;
    private List<String> details;
    private String suggestion;
    private String path;
    private String requestId;
    private LocalDateTime timestamp;

    public ErrorResponse(
            String code,
            Integer status,
            String message,
            String userMessage,
            List<String> details,
            String suggestion,
            String path,
            String requestId
    ) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.userMessage = userMessage;
        this.details = details;
        this.suggestion = suggestion;
        this.path = path;
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public List<String> getDetails() {
        return details;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getPath() {
        return path;
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
