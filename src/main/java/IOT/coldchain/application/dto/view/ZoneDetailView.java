package IOT.coldchain.application.dto.view;

import IOT.coldchain.domain.enums.ZoneStatus;

import java.util.List;

/**
 * READ MODEL — ZoneDetailView
 *
 * A flat projection of a single CompartmentZone, embedded inside
 * TruckDetailView or returned standalone by GetZoneDetailQuery.
 *
 * Contains full sensor and cargo box detail for this zone.
 *
 * @param zoneId       unique zone identifier
 * @param zoneName     human-readable zone name (e.g., "ZONE_A")
 * @param status       current threat level of this zone
 * @param minTemp      minimum safe temperature in °C
 * @param maxTemp      maximum safe temperature in °C
 * @param sensors      list of installed sensors with their latest reading
 * @param vaccineBoxes list of vaccine boxes currently loaded in this zone
 */
public record ZoneDetailView(
        String zoneId,
        String zoneName,
        ZoneStatus status,
        double minTemp,
        double maxTemp,
        List<SensorView> sensors,
        List<VaccineBoxView> vaccineBoxes
) {}