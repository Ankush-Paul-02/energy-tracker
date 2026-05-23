package com.paul.ingestionservice.controller;

import com.paul.ingestionservice.dto.EnergyUsesDto;
import com.paul.ingestionservice.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping("/ingest")
    @ResponseStatus(HttpStatus.CREATED)
    public void ingestData(@RequestBody EnergyUsesDto energyUsesDto) {
        ingestionService.ingestEnergyUses(energyUsesDto);
    }
}
