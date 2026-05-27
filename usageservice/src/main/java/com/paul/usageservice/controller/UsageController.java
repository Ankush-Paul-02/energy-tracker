package com.paul.usageservice.controller;

import com.paul.usageservice.dto.UsageDto;
import com.paul.usageservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UsageDto> getUserDeviceUsageByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "3") int days
    ) {
        final UsageDto response = usageService.getXDaysUsageForUser(userId, days);
        return ResponseEntity.ok(response);
    }
}
