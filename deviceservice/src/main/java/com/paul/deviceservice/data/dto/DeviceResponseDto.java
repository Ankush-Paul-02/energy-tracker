package com.paul.deviceservice.data.dto;

import com.paul.deviceservice.data.enums.DeviceType;
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
    private DeviceType type;
    private String location;
    private Long userId;
}
