package com.paul.ingestionservice.simulation;

import com.paul.ingestionservice.dto.EnergyUsesDto;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParallelDataSimulator implements CommandLineRunner {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Random random = new Random();
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${simulation.parallel.threads}")
    private int parallelThreads; // Number of parallel threads
    @Value("${simulation.request-per-interval}")
    private int requestPerInterval;
    @Value("${simulation.ingestion-endpoint}")
    private String ingestionEndpoint;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        log.info("ParallelDataSimulator started");
        ((ThreadPoolExecutor) executorService).setCorePoolSize(parallelThreads);
    }

    @Scheduled(fixedRateString = "${simulation.fixed-rate-ms}")
    public void sendMockData() {
        int batchSize = requestPerInterval / parallelThreads;
        int remainder = requestPerInterval % parallelThreads;

        for (int i = 0; i < parallelThreads; i++) {
            int requestForThreads = batchSize + (i < remainder ? 1 : 0);
            for (int j = 0; j < requestForThreads; j++) {
                executorService.submit(() -> {
                    EnergyUsesDto energyUsesDto = EnergyUsesDto.builder()
                            .deviceId(random.nextLong(1, 200))
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
                });
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
