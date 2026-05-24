package com.paul.usageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDto {
    private Long id;
    private String name;
    private String type;
    private String location;
    private Long userId;
}
