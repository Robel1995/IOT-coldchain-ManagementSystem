package IOT.coldchain.application.dto.view;

import java.time.Instant;

/**
 * READ MODEL — SensorView
 *
 * A flat projection of a TemperatureSensor entity, embedded inside ZoneDetailView.
 *
 * Includes the sensor's latest reading for dashboard display.
 * Full reading history is returned by GetSensorReadingHistoryQuery separately
 * to avoid bloating the main truck detail response.
 *
 * @param macAddress          IEEE 802 MAC address of the sensor (its identity)
 * @param location            physical location descriptor (e.g., "TOP", "DOOR")
 * @param latestTemperature   the most recent temperature reading value in °C,
 *                            or null if no readings have been recorded yet
 * @param latestReadingTime   the timestamp of the most recent reading,
 *                            or null if no readings have been recorded yet
 * @param totalReadingsCount  total number of readings recorded by this sensor
 */
public record SensorView(
        String macAddress,
        String location,
        Double latestTemperature,
        Instant latestReadingTime,
        int totalReadingsCount
) {}