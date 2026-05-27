package com.paul.usageservice.dto;

import lombok.Builder;

@Builder
public record DeviceResponseDto(
        Long id,
        String name,
        String type,
        String location,
        Long userId,
        double energyConsumed
) {
}
