package com.example.alertservice.service;

import com.example.kafka.event.AlertingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    @KafkaListener(topics = "energy_alerts", groupId = "alert-service")
    public void energyUsagesAlertEvents(AlertingEvent event) {
        log.info("Received event {}", event);
    }
}
