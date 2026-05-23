package com.paul.ingestionservice.service;

import com.paul.ingestionservice.dto.EnergyUsesDto;
import com.paul.kafka.event.EnergyUsesEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final KafkaTemplate<String, EnergyUsesEvent> kafkaTemplate;

    public void ingestEnergyUses(EnergyUsesDto energyUsesDto) {
        EnergyUsesEvent event = EnergyUsesEvent.builder()
                .deviceId(energyUsesDto.deviceId())
                .energyConsumption(energyUsesDto.energyConsumption())
                .timestamp(energyUsesDto.timestamp())
                .build();
        kafkaTemplate.send("energy-usage", event);
        log.info("Ingested energy uses event: {}", event);
    }
}
