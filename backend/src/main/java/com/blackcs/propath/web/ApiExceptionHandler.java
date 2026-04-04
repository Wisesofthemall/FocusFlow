package com.blackcs.propath.web;

import com.blackcs.propath.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    String message = ex.getMessage() != null ? ex.getMessage() : "Bad request";
    if (message.contains("not found")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(message));
    }
    if (message.contains("does not belong")) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(message));
    }
    if (message.contains("already registered")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(message));
    }
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .orElse("Validation failed");
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }
}
