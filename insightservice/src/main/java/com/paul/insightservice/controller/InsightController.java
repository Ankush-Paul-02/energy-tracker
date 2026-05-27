package com.paul.insightservice.controller;

import com.paul.insightservice.dto.InsightDto;
import com.paul.insightservice.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/insights")
public class InsightController {

    private final InsightService insightService;

    @GetMapping("/saving-tips/user/{userId}")
    public ResponseEntity<InsightDto> getInsight(@PathVariable Long userId) {
        final InsightDto insightDto = insightService.getSavingTips(userId);
        return ResponseEntity.ok(insightDto);
    }

    @GetMapping("/overview/user/{userId}")
    public ResponseEntity<InsightDto> getOverview(@PathVariable Long userId) {
        final InsightDto insightDto = insightService.getOverview(userId);
        return ResponseEntity.ok(insightDto);
    }
}
