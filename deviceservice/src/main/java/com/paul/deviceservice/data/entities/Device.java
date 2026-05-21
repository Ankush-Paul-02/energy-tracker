package com.paul.deviceservice.data.entities;

import com.paul.deviceservice.data.enums.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "devices")
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DeviceType type;

    private String location;

    @Column(name = "user_id")
    private Long userId;
}

