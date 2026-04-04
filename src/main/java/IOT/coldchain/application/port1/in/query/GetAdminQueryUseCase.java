package IOT.coldchain.application.port.in.query;

import IOT.coldchain.application.dto.query.GetAdminByIdQuery;
import IOT.coldchain.application.dto.query.GetAllAdminsQuery;
import IOT.coldchain.application.dto.view.AdminDetailView;
import IOT.coldchain.application.dto.view.AdminSummaryView;

import java.util.List;

/**
 * INBOUND PORT (USE CASE) — GetAdminQueryUseCase
 *
 * Defines the system's read capabilities for the AdminUser aggregate.
 *
 * ─── CQRS Read Side ────────────────────────────────────────────────────────────
 * All handlers on this interface are read-only. They return flat view objects
 * and never load or mutate domain aggregates. No @Transactional required.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * Admin queries are isolated from driver, truck, dispatcher, and health center
 * queries to maintain clean separation of concerns.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.GetAdminQueryHandler
 */
public interface GetAdminQueryUseCase {

    /**
     * Retrieves the full profile of a single AdminUser.
     *
     * @param query contains the adminId to retrieve
     * @return an AdminDetailView projection including contact information
     * @throws IllegalArgumentException if no admin exists with the given adminId
     */
    AdminDetailView handle(GetAdminByIdQuery query);

    /**
     * Retrieves a paginated list of all registered AdminUsers.
     *
     * @param query contains pagination parameters (page, size)
     * @return list of AdminSummaryView projections, empty list if none exist
     */
    List<AdminSummaryView> handle(GetAllAdminsQuery query);
}
