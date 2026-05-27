package com.paul.insightservice.service;

import com.paul.insightservice.client.UsageClient;
import com.paul.insightservice.dto.DeviceResponseDto;
import com.paul.insightservice.dto.InsightDto;
import com.paul.insightservice.dto.UsageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final UsageClient usageClient;
    private final ChatClient chatClient;

    public InsightDto getSavingTips(Long userId) {
        try {
            UsageDto usageData = fetchUsageData(userId);

            if (hasNoData(usageData)) {
                return createEmptyResponse(userId);
            }

            double totalUsage = calculateTotalUsage(usageData);
            String prompt = buildSavingTipsPrompt(totalUsage);

            String response = callOllamaWithRetry(prompt);

            return buildInsightResponse(userId, response, totalUsage);

        } catch (Exception e) {
            log.error("Error getting saving tips for userId: {}", userId, e);
            return createErrorResponse(userId, "Failed to generate tips: " + e.getMessage());
        }
    }

    public InsightDto getOverview(Long userId) {
        try {
            UsageDto usageData = fetchUsageData(userId);

            if (hasNoData(usageData)) {
                return createEmptyResponse(userId);
            }

            double totalUsage = calculateTotalUsage(usageData);
            String deviceSummary = buildDeviceSummary(usageData);
            String prompt = buildOverviewPrompt(deviceSummary);

            String response = callOllamaWithRetry(prompt);

            return buildInsightResponse(userId, response, totalUsage);

        } catch (Exception e) {
            log.error("Error getting overview for userId: {}", userId, e);
            return createErrorResponse(userId, "Failed to generate overview: " + e.getMessage());
        }
    }

    private UsageDto fetchUsageData(Long userId) {
        try {
            log.debug("Fetching usage data for userId: {}", userId);
            UsageDto usageData = usageClient.getXDaysUsageForUser(userId, 3).getBody();

            if (usageData == null) {
                log.warn("Received null usage data for userId: {}", userId);
            }

            return usageData;
        } catch (Exception e) {
            log.error("Failed to fetch usage data for userId: {}", userId, e);
            throw new RuntimeException("Failed to fetch usage data", e);
        }
    }

    private boolean hasNoData(UsageDto usageData) {
        return usageData == null ||
                usageData.devices() == null ||
                usageData.devices().isEmpty();
    }

    private double calculateTotalUsage(UsageDto usageData) {
        return usageData.devices().stream()
                .mapToDouble(DeviceResponseDto::energyConsumed)
                .sum();
    }

    private String buildDeviceSummary(UsageDto usageData) {
        return usageData.devices().stream()
                .map(device -> String.format(
                        "Device: %s\nEnergy Consumed: %.2f kWh\n",
                        device.name(),
                        device.energyConsumed()
                ))
                .reduce("", String::concat);
    }

    private String buildSavingTipsPrompt(double totalUsage) {
        return String.format("""
                This is my total energy consumption over the past 3 days.
                
                Total energy used: %.2f kWh
                
                How can I reduce my energy consumption?
                How does this compare to average households?
                
                Provide concise and actionable recommendations.
                Keep response under 200 words.
                """, totalUsage);
    }

    private String buildOverviewPrompt(String deviceSummary) {
        return String.format("""
                Analyze the following household energy usage data collected over the past 3 days.
                
                %s
                
                Provide:
                - overall consumption summary
                - highest consuming devices
                - unusual patterns
                - practical energy-saving recommendations
                
                Keep the response concise and practical.
                Limit response to 250 words.
                """, deviceSummary);
    }

    private String callOllamaWithRetry(String prompt) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                log.info("Calling Ollama (attempt {})", i + 1);
                String response = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
                log.info("Ollama response received");
                return response;
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    log.error("Ollama failed after {} attempts", maxRetries);
                    return "AI service temporarily unavailable.";
                }
                log.warn("Attempt {} failed, retrying...", i + 1);
                try {
                    Thread.sleep(2000 * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "AI service temporarily unavailable.";
    }

    private InsightDto buildInsightResponse(Long userId, String response, double totalUsage) {
        return InsightDto.builder()
                .userId(userId)
                .tips(response != null ? response : "AI service unavailable.")
                .energyUsage(totalUsage)
                .build();
    }

    private InsightDto createEmptyResponse(Long userId) {
        log.info("No usage data found for userId: {}", userId);
        return InsightDto.builder()
                .userId(userId)
                .tips("No usage data available for analysis. Start using your devices to get insights!")
                .energyUsage(0)
                .build();
    }

    private InsightDto createErrorResponse(Long userId, String errorMessage) {
        return InsightDto.builder()
                .userId(userId)
                .tips("Unable to generate insights at this moment. " + errorMessage)
                .energyUsage(0)
                .build();
    }
}