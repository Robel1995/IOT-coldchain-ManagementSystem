package IOT.coldchain.application.dto.view;

/**
 * READ MODEL — VaccineBoxView
 *
 * A flat projection of a VaccineBox entity, embedded inside ZoneDetailView.
 *
 * @param serialNumber  globally unique box identifier
 * @param medicineName  name of the medicine in this box (e.g., "Polio Vaccine OPV")
 * @param isRuined      true if this box was marked ruined during a spoil cascade
 */
public record VaccineBoxView(
        String serialNumber,
        String medicineName,
        boolean isRuined
) {}