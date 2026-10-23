package com.medconnect.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Human-readable error message", example = "Invalid request parameters")
    private String message;

    @Schema(description = "Detailed error description")
    private String details;

    @Schema(description = "Request path", example = "/api/search")
    private String path;

    @Schema(description = "Timestamp of the error")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Validation errors (for validation failures)")
    private Map<String, String> validationErrors;

    public static ErrorResponseDTO of(int status, String errorCode, String message, String path) {
        return ErrorResponseDTO.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponseDTO validationError(Map<String, String> errors, String path) {
        return ErrorResponseDTO.builder()
                .status(400)
                .errorCode("VALIDATION_ERROR")
                .message("Request validation failed")
                .validationErrors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}