package com.paul.usageservice.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.paul.kafka.event.AlertingEvent;
import com.paul.kafka.event.EnergyUsesEvent;
import com.paul.usageservice.client.DeviceClient;
import com.paul.usageservice.client.UserClient;
import com.paul.usageservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageService {

    private final InfluxDBClient influxDbClient;
    private final DeviceClient deviceClient;
    private final UserClient userClient;
    private final KafkaTemplate<String, AlertingEvent> kafkaTemplate;
    @Value("${influx.bucket}")
    private String influxBucket;
    @Value("${influx.org}")
    private String influxOrg;

    @KafkaListener(topics = "energy-usage", groupId = "usage-service")
    public void consumeEnergyUsageEvent(EnergyUsesEvent event) {

        log.info("Received event: {}", event);

        Point point = Point.measurement("energy-usage")
                .addTag("deviceId", String.valueOf(event.deviceId()))
                .addField("energyConsumed", event.energyConsumption())
                .time(event.timestamp(), WritePrecision.MS);

        influxDbClient
                .getWriteApiBlocking()
                .writePoint(influxBucket, influxOrg, point);

        log.info("Data written to InfluxDB");
    }

    @Scheduled(cron = "*/10 * * * * *") // 10 sec
    public void aggregateDeviceEnergyUsages() {
        final Instant now = Instant.now();
        final Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);

        // get data from influxdb
        List<FluxTable> fluxTables = getFluxTables(oneHourAgo, now);

        List<DeviceEnergy> deviceEnergies = new ArrayList<>();
        for (FluxTable table : fluxTables) {
            for (FluxRecord record : table.getRecords()) {
                String deviceIdStr = (String) record.getValueByKey("deviceId");
                double energyConsumed = record.getValueByKey("_value") instanceof Number ?
                        ((Number) Objects.requireNonNull(record.getValueByKey("_value"))).doubleValue() : 0.0;
                if (deviceIdStr == null) {
                    continue;
                }

                deviceEnergies.add(
                        DeviceEnergy.builder()
                                .deviceId(Long.valueOf(deviceIdStr))
                                .energyConsumed(energyConsumed)
                                .build()
                );
            }
        }

        log.info("Aggregated device energies over the past hour: {}", deviceEnergies);

        for (DeviceEnergy deviceEnergy : deviceEnergies) {
            try {
                final DeviceResponseDto deviceResponseDto = deviceClient.getDeviceById(deviceEnergy.getDeviceId()).getData();

                if (deviceResponseDto == null || deviceResponseDto.name() == null) {
                    log.warn("No device found for deviceId: {}", deviceEnergy.getDeviceId());
                    continue;
                }
                deviceEnergy.setUserId(deviceResponseDto.userId());
            } catch (Exception e) {
                log.warn("Error getting device user id from deviceId: {}", deviceEnergy.getDeviceId());
            }
        }

        // remove device energies with no user id
        deviceEnergies.removeIf(deviceEnergy -> deviceEnergy.getUserId() == null);

        // get user device mapping and aggregate per user
        Map<Long, List<DeviceEnergy>> userDeviceMap = deviceEnergies.stream()
                .collect(Collectors.groupingBy(DeviceEnergy::getUserId));

        log.info("User-Device Energy Map: {}", userDeviceMap);

        // get user energy consumption threshold
        List<Long> userIds = new ArrayList<>(userDeviceMap.keySet());
        Map<Long, Double> userEnergyThresholdMap = new HashMap<>();
        Map<Long, String> userEmailMap = new HashMap<>();

        for (final Long id : userIds) {
            try {
                UserResponseDto userResponse = userClient.getUserById(id).getData();

                if (userResponse == null || userResponse.getId() == null || !userResponse.isAlerting()) {
                    log.warn("User not found or alerting disabled for ID: {}", id);
                    continue;
                }
                userEnergyThresholdMap.put(id, userResponse.getEnergyAlertingThreshold());
                userEmailMap.put(id, userResponse.getEmail());
            } catch (Exception e) {
                log.error("Failed to fetch user for ID: {}", id, e);
            }
        }
        log.info("User Threshold Map: {}", userEnergyThresholdMap);

        // check thresholds againtst aggregated uses
        final List<Long> alertedUsers = new ArrayList<>(userEnergyThresholdMap.keySet());

        for (final Long id : alertedUsers) {
            final double threshold = userEnergyThresholdMap.get(id);
            final List<DeviceEnergy> devices = userDeviceMap.get(id);

            final double totalConsumption = devices.stream()
                    .mapToDouble(DeviceEnergy::getEnergyConsumed)
                    .sum();

            if (totalConsumption > threshold) {
                log.info("ALERT: User ID {} has exceeded the energy threshold! " +
                                "Total Consumption: {}, Threshold: {}",
                        id, totalConsumption, threshold);
                // Put message on kafak alert-topic
                final AlertingEvent alertingEvent = AlertingEvent.builder()
                        .userId(id)
                        .message("Energy consumption threshold exceeded")
                        .threshold(threshold)
                        .energyConsumed(totalConsumption)
                        .email(userEmailMap.get(id))
                        .build();
                kafkaTemplate.send("energy-alerts", alertingEvent);
            } else {
                log.info("User ID {} is within the energy threshold. " +
                                "Total Consumption: {}, Threshold: {}",
                        id, totalConsumption, threshold);
            }
        }
    }

    private @NonNull List<FluxTable> getFluxTables(Instant oneHourAgo, Instant now) {
        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: time(v: "%s"), stop: time(v: "%s"))
                  |> filter(fn: (r) => r["_measurement"] == "energy-usage")
                  |> filter(fn: (r) => r["_field"] == "energyConsumed")
                  |> group(columns: ["deviceId"])
                  |> sum(column: "_value")
                """, influxBucket, oneHourAgo.toString(), now);

        QueryApi queryApi = influxDbClient.getQueryApi();
        return queryApi.query(fluxQuery, influxOrg);
    }

    public UsageDto getXDaysUsageForUser(Long userId, int days) {
        log.info("Getting usage for userId: {}, over past {} days.", userId, days);
        final List<DeviceResponseDto> devicesDto = deviceClient.getAllDevicesByUserId(userId).getData();

        final List<Device> devices = new ArrayList<>();
        for (DeviceResponseDto deviceDto : devicesDto) {
            devices.add(Device.builder()
                    .id(deviceDto.id())
                    .name(deviceDto.name())
                    .type(deviceDto.type())
                    .location(deviceDto.location())
                    .userId(deviceDto.userId())
                    .build());
        }

        if (devices.isEmpty()) {
            return UsageDto.builder()
                    .userId(userId)
                    .devices(null)
                    .build();
        }

        List<String> deviceIdStrings = devices.stream()
                .map(Device::getId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();

        final Instant now = Instant.now();
        final Instant startTime = Instant.now().minus(days, ChronoUnit.DAYS);

        final String deviceFilter = deviceIdStrings.stream()
                .map(idStr -> String.format("r[\"deviceId\"] == \"%s\"", idStr))
                .collect(Collectors.joining(" or "));

        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: time(v: "%s"), stop: time(v: "%s"))
                  |> filter(fn: (r) => r["_measurement"] == "energy-usage")
                  |> filter(fn: (r) => r["_field"] == "energyConsumed")
                  |> filter(fn: (r) => %s)
                  |> group(columns: ["deviceId"])
                  |> sum(column: "_value")
                """, influxBucket, startTime.toString(), now.toString(), deviceFilter);

        final Map<Long, Double> aggregatedMap = new HashMap<>();

        try {
            QueryApi queryApi = influxDbClient.getQueryApi();
            List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);

            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    Object deviceIdObj = record.getValueByKey("deviceId");
                    String deviceIdStr = deviceIdObj == null ? null : deviceIdObj.toString();
                    if (deviceIdStr == null) continue;

                    Double energyConsumed = record.getValueByKey("_value") instanceof Number
                            ? ((Number) Objects.requireNonNull(record.getValueByKey("_value"))).doubleValue()
                            : 0.0;

                    try {
                        Long deviceId = Long.valueOf(deviceIdStr);
                        aggregatedMap.put(deviceId, aggregatedMap.getOrDefault(deviceId, 0.0) + energyConsumed);
                    } catch (NumberFormatException nfe) {
                        log.warn("Failed to parse deviceId from flux record: {}", deviceIdStr);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to query InfluxDB for user {} usage over {} days: {}", userId, days, e.getMessage());
            // set aggregatedConsumption to 0.0 on error
            devices.forEach(d -> d.setEnergyConsumed(0.0));
            return UsageDto.builder()
                    .userId(userId)
                    .devices(null)
                    .build();
        }

        // populate aggregated energy consumed per device
        for (Device device : devices) {
            if (device == null || device.getId() == null) continue;
            device.setEnergyConsumed(aggregatedMap.getOrDefault(device.getId(), 0.0));
        }

        log.info("Aggregated energy consumption for userId {}: {}", userId, aggregatedMap);

        final List<DeviceResponseDto> resultDevices = devices.stream()
                .map(d -> DeviceResponseDto.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .type(d.getType())
                        .location(d.getLocation())
                        .userId(d.getUserId())
                        .energyConsumed(d.getEnergyConsumed())
                        .build())
                .toList();

        return UsageDto.builder()
                .userId(userId)
                .devices(resultDevices)
                .build();
    }
}
