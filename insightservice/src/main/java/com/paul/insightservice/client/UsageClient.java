package com.paul.insightservice.client;

import com.paul.insightservice.dto.UsageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "usage-service",
        url = "${usage.service.url}"
)
public interface UsageClient {

    @GetMapping("/user/{userId}")
    ResponseEntity<UsageDto> getXDaysUsageForUser(@PathVariable Long userId, @RequestParam int days);
}
