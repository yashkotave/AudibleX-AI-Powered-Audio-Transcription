package com.audiblex.exception;

import com.audiblex.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(TranscriptionException.class)
    ResponseEntity<ApiError> handleTranscription(TranscriptionException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> details = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage(),
                        (first, second) -> first));
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), 400, "Request validation failed.", details));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiError> handleUploadSize(MaxUploadSizeExceededException exception, HttpServletRequest request) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "Audio upload exceeds the 25 MB limit.", request);
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<ApiError> handleMalformedMultipart(MultipartException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "The multipart audio upload could not be read.", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), message, Map.of("path", request.getRequestURI())));
    }
}
