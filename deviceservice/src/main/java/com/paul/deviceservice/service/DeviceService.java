package com.paul.deviceservice.service;

import com.paul.deviceservice.data.dto.DeviceRequestDto;
import com.paul.deviceservice.data.dto.DeviceResponseDto;
import com.paul.deviceservice.data.entities.Device;
import com.paul.deviceservice.data.exception.AppInfoException;
import com.paul.deviceservice.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceResponseDto createDevice(DeviceRequestDto deviceRequestDto) {
        Device device = Device.builder()
                .name(deviceRequestDto.getName())
                .type(deviceRequestDto.getType())
                .location(deviceRequestDto.getLocation())
                .userId(deviceRequestDto.getUserId())
                .build();

        Device savedDevice = deviceRepository.save(device);

        return buildDeviceResponseDto(savedDevice);
    }

    public DeviceResponseDto getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new AppInfoException(
                        "Device not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        return buildDeviceResponseDto(device);
    }

    public List<DeviceResponseDto> getAllDevices() {
        return deviceRepository.findAll()
                .stream()
                .map(this::buildDeviceResponseDto)
                .toList();
    }

    public List<DeviceResponseDto> getAllDevicesByUserId(Long userId) {
        return deviceRepository.findAllByUserId(userId)
                .stream().map(this::buildDeviceResponseDto)
                .toList();
    }

    public DeviceResponseDto updateDevice(Long id, DeviceRequestDto deviceRequestDto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new AppInfoException(
                        "Device not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        device.setName(deviceRequestDto.getName());
        device.setType(deviceRequestDto.getType());
        device.setLocation(deviceRequestDto.getLocation());
        device.setUserId(deviceRequestDto.getUserId());

        Device updatedDevice = deviceRepository.save(device);

        return buildDeviceResponseDto(updatedDevice);
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new AppInfoException(
                        "Device not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        deviceRepository.delete(device);
    }

    private DeviceResponseDto buildDeviceResponseDto(Device device) {
        return DeviceResponseDto.builder()
                .id(device.getId())
                .name(device.getName())
                .type(device.getType().name())
                .location(device.getLocation())
                .userId(device.getUserId())
                .build();
    }
}