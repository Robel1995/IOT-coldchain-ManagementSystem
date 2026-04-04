package IOT.coldchain.application.dto.view;

/**
 * READ MODEL — AdminSummaryView
 *
 * A lightweight projection of an AdminUser for list-based queries
 * (GetAllAdminsQuery).
 *
 * @param adminId   unique admin identifier
 * @param fullName  admin's full name
 * @param email     contact email address
 */
public record AdminSummaryView(
        String adminId,
        String fullName,
        String email
) {}