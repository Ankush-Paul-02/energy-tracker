package com.paul.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EnergyUsesEvent(
        Long deviceId,
        double energyConsumption,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant timestamp
) {
}