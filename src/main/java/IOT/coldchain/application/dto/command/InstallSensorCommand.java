package IOT.coldchain.application.dto.command;

/**
 * COMMAND — InstallSensorCommand
 *
 * Represents the user's intent to install a physical IoT temperature sensor
 * into a specific compartment zone of a truck.
 *
 * Handled by: ManageSensorUseCase
 * Domain method called: truck.installSensor(zoneId, macAddress, location)
 * Business rules enforced by domain:
 *   - Truck must not be SPOILED or SCRAPPED.
 *   - MAC address must be in valid IEEE 802 format (XX:XX:XX:XX:XX:XX).
 *   - No duplicate MAC address allowed within the same zone (LinkedHashSet enforces).
 *   - Location string must not be blank.
 *
 * @param truckId     the truck that contains the zone
 * @param zoneId      the zone where the sensor will be physically installed
 * @param macAddress  IEEE 802 MAC address of the sensor (e.g., "AA:BB:CC:DD:EE:FF")
 * @param location    physical location descriptor (e.g., "TOP", "DOOR", "FLOOR")
 */
public record InstallSensorCommand(
        String truckId,
        String zoneId,
        String macAddress,
        String location
) {}