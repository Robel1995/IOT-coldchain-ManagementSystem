package IOT.coldchain.application.dto.view;

import java.time.LocalDate;

/**
 * READ MODEL — DriverDetailView
 *
 * A flat projection of a TruckDriver aggregate for GetDriverByIdQuery.
 *
 * @param driverId       unique driver identifier
 * @param fullName       driver's full legal name
 * @param email          contact email address
 * @param phoneNumber    contact phone number
 * @param licenseNumber  driver's license number
 * @param licenseExpiry  license expiry date
 * @param licenseExpired true if the license has already expired
 */
public record DriverDetailView(
        String driverId,
        String fullName,
        String email,
        String phoneNumber,
        String licenseNumber,
        LocalDate licenseExpiry,
        boolean licenseExpired
) {}