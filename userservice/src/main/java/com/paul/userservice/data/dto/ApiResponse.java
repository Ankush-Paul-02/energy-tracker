package com.paul.userservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private int status;
    private String message;

    public static <T> ApiResponse<T> success(T data, int status, String message) {
        return new ApiResponse<>(data, status, message);
    }
}