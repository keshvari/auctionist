package com.example.auctionist.web;


import com.example.auctionist.support.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.util.stream.Collectors;


@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, msg, req.getRequestURI()));
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = ex.getStatus();
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status, ex.getMessage(), req.getRequestURI()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI()));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<com.example.auctionist.support.ErrorResponse> handleIllegalArg(
            IllegalArgumentException ex, jakarta.servlet.http.HttpServletRequest req) {
        var status = org.springframework.http.HttpStatus.BAD_REQUEST;
        return org.springframework.http.ResponseEntity.status(status)
                .body(com.example.auctionist.support.ErrorResponse.of(status, ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req){
        String msg = "Invalid value for '" + ex.getName() + "': '" + ex.getValue()
                + "'. Allowed: " + java.util.Arrays.toString(com.example.auctionist.domain.ItemStatus.values());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST,msg,req.getRequestURI()));
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<com.example.auctionist.support.ErrorResponse> handleOptimistic(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex,
            jakarta.servlet.http.HttpServletRequest req) {
        var status = org.springframework.http.HttpStatus.CONFLICT;
        return ResponseEntity.status(status)
                .body(com.example.auctionist.support.ErrorResponse.of(status, "Concurrent update, please retry", req.getRequestURI()));
    }

}