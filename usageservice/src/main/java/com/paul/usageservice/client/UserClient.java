package com.paul.usageservice.client;

import com.paul.usageservice.dto.ApiResponse;
import com.paul.usageservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${user.service.url}"
)
public interface UserClient {

    @GetMapping("/{id}")
    ApiResponse<UserResponseDto> getUserById(@PathVariable Long id);
}