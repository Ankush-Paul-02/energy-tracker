package com.paul.ingestionservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EnergyUsesDto(
        Long deviceId,
        double energyConsumption,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant timestamp
) {
}
