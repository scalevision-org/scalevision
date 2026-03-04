package com.scalevision.backend.config;

import com.scalevision.backend.dto.ErrorResponse;
import com.scalevision.backend.exception.BadRequestException;
import com.scalevision.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        "No encontramos el recurso solicitado.",
                        List.of(ex.getMessage()),
                        "Verifica el identificador e intenta de nuevo.",
                        request
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        "BAD_REQUEST",
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        "La solicitud no es valida para el estado actual del video.",
                        List.of(ex.getMessage()),
                        "Revisa los datos enviados y vuelve a intentar.",
                        request
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        "VALIDATION_ERROR",
                        HttpStatus.BAD_REQUEST,
                        "Error de validacion en la solicitud",
                        "Uno o mas campos tienen valores invalidos.",
                        details,
                        "Corrige los campos indicados y vuelve a enviar la peticion.",
                        request
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        "INTERNAL_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error inesperado en el servidor",
                        "Tuvimos un problema interno al procesar tu solicitud.",
                        List.of(ex.getClass().getSimpleName()),
                        "Intenta de nuevo en unos segundos. Si persiste, contacta soporte.",
                        request
                ));
    }

    private ErrorResponse buildError(
            String code,
            HttpStatus status,
            String message,
            String userMessage,
            List<String> details,
            String suggestion,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                code,
                status.value(),
                message,
                userMessage,
                details,
                suggestion,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );
    }
}
