package com.paul.alertservice.service;

import com.paul.alertservice.data.entities.Alert;
import com.paul.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final AlertRepository alertRepository;

    public void sendEmail(String to, String subject, String body, Long userId) {
        log.info("Sending email to {} with subject {}", to, subject);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("noreply@paul.com");
        message.setSubject(subject);
        message.setText(body);

        Alert alert;
        try {
            javaMailSender.send(message);

            alert = Alert.builder()
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .sent(true)
                    .build();

            alertRepository.saveAndFlush(alert);

        } catch (Exception e) {
            log.error("Error while sending email to {} with subject {}", to, subject, e);
            alert = Alert.builder()
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .sent(false)
                    .build();
            alertRepository.saveAndFlush(alert);
            return;
        }
        log.info("Email sent to {} with subject {}", to, subject);
    }
}
