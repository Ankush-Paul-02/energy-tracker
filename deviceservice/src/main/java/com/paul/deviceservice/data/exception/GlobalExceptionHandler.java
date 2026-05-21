package com.paul.deviceservice.data.exception;

import com.paul.deviceservice.data.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
        ApiResponse<Object> response = new ApiResponse<>(
                null,
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong,"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(AppInfoException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppInfoException(AppInfoException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiResponse<>(
                        null,
                        ex.getStatus().value(),
                        ex.getMessage()
                ));
    }
}
