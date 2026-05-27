package com.paul.deviceservice.controller;

import com.paul.deviceservice.data.dto.ApiResponse;
import com.paul.deviceservice.data.dto.DeviceRequestDto;
import com.paul.deviceservice.data.dto.DeviceResponseDto;
import com.paul.deviceservice.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DeviceResponseDto>> createDevice(
            @RequestBody DeviceRequestDto deviceRequestDto
    ) {
        DeviceResponseDto deviceResponseDto = deviceService.createDevice(deviceRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        deviceResponseDto,
                        HttpStatus.CREATED.value(),
                        "Device created successfully"
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponseDto>> getDeviceById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        deviceService.getDeviceById(id),
                        HttpStatus.OK.value(),
                        "Device fetched successfully"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceResponseDto>>> getAllDevices() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        deviceService.getAllDevices(),
                        HttpStatus.OK.value(),
                        "Devices fetched successfully"
                ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DeviceResponseDto>>> getAllDevicesByUserId(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        deviceService.getAllDevicesByUserId(userId),
                        HttpStatus.OK.value(),
                        "All Devices fetched successfully for user: " + userId
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponseDto>> updateDevice(
            @PathVariable Long id,
            @RequestBody DeviceRequestDto deviceRequestDto
    ) {
        DeviceResponseDto deviceResponseDto = deviceService.updateDevice(id, deviceRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        deviceResponseDto,
                        HttpStatus.OK.value(),
                        "Device updated successfully"
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        null,
                        HttpStatus.OK.value(),
                        "Device deleted successfully"
                ));
    }
}