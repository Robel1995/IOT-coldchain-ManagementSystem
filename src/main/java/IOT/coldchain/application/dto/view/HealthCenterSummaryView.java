package IOT.coldchain.application.dto.view;

/**
 * READ MODEL — HealthCenterSummaryView
 *
 * A lightweight projection of a HealthCenter for list-based queries
 * (GetAllHealthCentersQuery).
 *
 * @param centerId         unique health center identifier
 * @param centerName       name of the facility
 * @param email            contact email address
 * @param pendingOrderCount number of shipment orders currently pending at this center
 */
public record HealthCenterSummaryView(
        String centerId,
        String centerName,
        String email,
        int pendingOrderCount
) {}