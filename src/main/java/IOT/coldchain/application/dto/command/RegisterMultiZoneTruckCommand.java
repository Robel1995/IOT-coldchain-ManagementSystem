package IOT.coldchain.application.dto.command;

import java.util.List;

/**
 * COMMAND — RegisterMultiZoneTruckCommand
 *
 * Represents the user's intent to register a refrigerated truck where the
 * full zone layout is known upfront. Each zone gets its own temperature range.
 *
 * Example: A truck carrying both polio vaccines (2–8°C) and yellow fever (−20 to −15°C)
 * needs two zones defined at registration time.
 *
 * Handled by: RegisterTruckUseCase
 * Domain method called: RefrigeratedTruckFactory.createNewMultiZone(...)
 *
 * @param zoneDefinitions  list of zone specs — must contain at least one entry
 */
public record RegisterMultiZoneTruckCommand(
        List<ZoneDefinitionDto> zoneDefinitions
) {
    /**
     * Inner DTO — defines a single zone's name and temperature range.
     *
     * @param zoneName  name of the compartment zone (e.g., "ZONE_A", "COLD_1")
     * @param minTemp   minimum safe temperature in °C
     * @param maxTemp   maximum safe temperature in °C
     */
    public record ZoneDefinitionDto(
            String zoneName,
            double minTemp,
            double maxTemp
    ) {}
}