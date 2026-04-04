package IOT.coldchain.application.dto.view;

import java.time.LocalDate;

/**
 * READ MODEL — DriverSummaryView
 *
 * A lightweight projection of a TruckDriver for list-based queries
 * (GetAllDriversQuery).
 *
 * @param driverId       unique driver identifier
 * @param fullName       driver's full name
 * @param licenseExpiry  license expiry date
 * @param licenseExpired true if the license has already expired
 */
public record DriverSummaryView(
        String driverId,
        String fullName,
        LocalDate licenseExpiry,
        boolean licenseExpired
) {}