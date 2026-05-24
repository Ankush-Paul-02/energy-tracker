package com.paul.usageservice.client;

import com.paul.usageservice.dto.ApiResponse;
import com.paul.usageservice.dto.DeviceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "device-service",
        url = "${device.service.url}"
)
public interface DeviceClient {

    @GetMapping("/{id}")
    ApiResponse<DeviceResponseDto> getDeviceById(@PathVariable Long id);
}
