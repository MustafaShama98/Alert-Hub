package org.example.metricservice.ExecptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors for @Valid annotated request bodies
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // Handles JPA/Hibernate constraint violations like missing NOT NULL fields
    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<Map<String, String>> handleHibernatePropertyValueException(PropertyValueException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Missing or null required field: " + ex.getPropertyName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // (Optional) Handles invalid query/path params, not request body
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Constraint violation: " + ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

//    // (Optional) Generic fallback for other exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
//        Map<String, String> error = new HashMap<>();
//        error.put("error", "Internal error: " + ex.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }
}