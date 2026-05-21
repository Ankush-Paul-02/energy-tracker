package com.paul.deviceservice.data.dto;

import com.paul.deviceservice.data.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequestDto {
    private String name;
    private DeviceType type;
    private String location;
    private Long userId;
}