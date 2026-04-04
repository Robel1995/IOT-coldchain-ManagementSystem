package IOT.coldchain.application.dto.view;

/**
 * READ MODEL — AdminDetailView
 *
 * A flat projection of an AdminUser aggregate for GetAdminByIdQuery.
 *
 * @param adminId     unique admin identifier
 * @param fullName    admin's full name
 * @param email       contact email address
 * @param phoneNumber contact phone number
 */
public record AdminDetailView(
        String adminId,
        String fullName,
        String email,
        String phoneNumber
) {}