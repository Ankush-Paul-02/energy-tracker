package com.paul.alertservice.service;

import com.paul.kafka.event.AlertingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final EmailService emailService;

    @KafkaListener(topics = "energy-alerts", groupId = "alert-service")
    public void energyUsagesAlertEvents(AlertingEvent event) {
        log.info("Received event {}", event);

        final String subject = "Energy usages alert for user: " + event.userId();
        final String message = "Alert: " + event.message() +
                "\nThreshold: " + event.threshold() +
                "\nEnergy Consumed: " + event.energyConsumed();

        emailService.sendEmail(event.email(), subject, message, event.userId());
    }
}
