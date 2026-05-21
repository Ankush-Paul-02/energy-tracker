package com.paul.deviceservice;

import com.paul.deviceservice.data.entities.Device;
import com.paul.deviceservice.data.enums.DeviceType;
import com.paul.deviceservice.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class DeviceserviceApplicationTests {

    private final int numberOfDevices = 200;
    private final int numberOfUsers = 10;

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void createDevice() {
        for (int i = 1; i <= numberOfDevices; i++) {
            Device device = Device.builder()
                    .name("device" + i)
                    .type(DeviceType.values()[i % DeviceType.values().length])
                    .location("location" + i)
                    .userId((long) (i % numberOfUsers) + 1)
                    .build();
            deviceRepository.save(device);
        }
        log.info("Devices created successfully");
    }

}
