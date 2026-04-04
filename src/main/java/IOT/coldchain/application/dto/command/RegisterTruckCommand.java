package IOT.coldchain.application.dto.command;

/**
 * COMMAND — RegisterTruckCommand
 *
 * Represents the user's intent to register a new refrigerated truck into the
 * EPSS cold-chain monitoring system with one initial compartment zone.
 *
 * This is the simplest truck creation path. For trucks with multiple zones
 * known upfront, use RegisterMultiZoneTruckCommand.
 *
 * Handled by: RegisterTruckUseCase
 * Domain method called: RefrigeratedTruckFactory.createNew(...)
 *
 * Records are immutable by design — a Command must never be mutated
 * after it is created. Java records enforce this automatically.
 *
 * @param initialZoneName  name of the first compartment zone (e.g., "ZONE_A")
 * @param minTemp          minimum safe temperature for the zone in °C
 * @param maxTemp          maximum safe temperature for the zone in °C
 */
public record RegisterTruckCommand(
        String initialZoneName,
        double minTemp,
        double maxTemp
) {}