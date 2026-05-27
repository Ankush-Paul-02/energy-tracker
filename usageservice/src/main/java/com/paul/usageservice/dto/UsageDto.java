package com.paul.usageservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UsageDto(
        Long userId,
        List<DeviceResponseDto> devices
) {
}
