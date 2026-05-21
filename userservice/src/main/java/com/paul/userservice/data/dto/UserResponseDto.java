package com.paul.userservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private long id;
    private String username;
    private String email;
    private String address;
    private boolean isAlerting;
    private double energyAlertingThreshold;
}
