package IOT.coldchain.application.dto.query;

/**
 * QUERY — GetDriverCommandHistoryQuery
 *
 * Represents the intent to retrieve the full admin-command history for a
 * specific truck, including all commands issued to its assigned driver.
 *
 * Returns all DriverCommand records (ISSUED, ACKNOWLEDGED, COMPLETED, REJECTED)
 * in chronological order (most recent first).
 *
 * Returns projection list, not aggregates.
 *
 * Handled by: GetDriverQueryUseCase
 * Returns: List<DriverCommandView>
 *
 * @param truckId  the truck whose command history is requested
 */
public record GetDriverCommandHistoryQuery(
        String truckId
) {}
