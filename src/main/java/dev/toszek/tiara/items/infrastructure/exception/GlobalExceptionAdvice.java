package dev.toszek.tiara.items.infrastructure.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestControllerAdvice
class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(error -> {
            String errorMessage = error.getField() + ": " + error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(error -> {
            String errorMessage = getFieldFromPath(error.getPropertyPath()) + ": " + error.getMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    private String getFieldFromPath(Path fieldPath) {
        Iterator<Path.Node> nodes = fieldPath.iterator();
        String fieldName = null;
        while (nodes.hasNext()) {
            fieldName = nodes.next().toString();
        }
        return fieldName;
    }
}
