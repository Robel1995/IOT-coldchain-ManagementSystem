package IOT.coldchain.application.dto.view;

import java.time.Instant;

/**
 * READ MODEL — TemperatureReadingView
 *
 * A flat projection of a single Temperature value object from a sensor's
 * reading history. Returned as a list by GetSensorReadingHistoryQuery.
 *
 * Ordered chronologically (oldest first) by the handler.
 *
 * @param value       the temperature reading in °C
 * @param recordedAt  the exact instant this reading was captured
 */
public record TemperatureReadingView(
        double value,
        Instant recordedAt
) {}