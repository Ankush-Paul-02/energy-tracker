package com.paul.userservice.controller;

import com.paul.userservice.data.dto.ApiResponse;
import com.paul.userservice.data.dto.UserRequestDto;
import com.paul.userservice.data.dto.UserResponseDto;
import com.paul.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        userResponseDto,
                        HttpStatus.CREATED.value(),
                        "User created successfully"
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        userResponseDto,
                        HttpStatus.OK.value(),
                        "User fetched successfully"
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        null,
                        HttpStatus.OK.value(),
                        "User deleted successfully"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto userRequestDto
    ) {
        UserResponseDto userResponseDto = userService.updateUser(id, userRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        userResponseDto,
                        HttpStatus.OK.value(),
                        "User updated successfully"
                ));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        userService.getAllUsers(),
                        HttpStatus.OK.value(),
                        "Users fetched successfully"
                ));
    }
}