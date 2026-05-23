package com.paul.ingestionservice.simulation;

import com.paul.ingestionservice.dto.EnergyUsesDto;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Random;

@Slf4j
@Component
public class ContinueousDataSimulator implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${simulation.request-per-interval}")
    private int requestPerInterval;

    @Value("${simulation.ingestion-endpoint}")
    private String ingestionEndpoint;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        log.info("ContinueousDataSimulator Started...");
    }

    //    @Scheduled(fixedRateString = "${simulation.fixed-rate-ms}")
    private void simulateMockData() {
        for (int i = 0; i < requestPerInterval; i++) {
            EnergyUsesDto energyUsesDto = EnergyUsesDto.builder()
                    .deviceId(random.nextLong(1, 6))
                    .energyConsumption((double) Math.round(random.nextDouble(0.0, 2.0) * 100) / 100)
                    .timestamp(Instant.now())
                    .build();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<EnergyUsesDto> entity = new HttpEntity<>(energyUsesDto, headers);

                restTemplate.postForEntity(ingestionEndpoint, entity, EnergyUsesDto.class);
                log.info("Send mock data to ingestion endpoint: {}", energyUsesDto);
            } catch (Exception e) {
                log.error("Failed to send mock data to ingestion: {}", e.getMessage());
            }
        }
    }
}
