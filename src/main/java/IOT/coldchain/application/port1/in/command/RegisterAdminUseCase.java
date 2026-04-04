package IOT.coldchain.application.port.in.command;

import IOT.coldchain.application.dto.command.RegisterAdminCommand;
import IOT.coldchain.application.dto.command.UpdateAdminContactCommand;

/**
 * INBOUND PORT (USE CASE) — RegisterAdminUseCase
 *
 * Defines the system's capabilities for managing AdminUser aggregates:
 * registration and contact information updates.
 *
 * ─── ISP Application ──────────────────────────────────────────────────────────
 * AdminUser management is isolated from all other aggregates. Admins are system
 * operators, not participants in the physical cold-chain process. Their interface
 * is intentionally small — they are registered and updated, nothing more.
 * Issuing commands to drivers is handled by IssueDriverCommandUseCase, which
 * only needs an adminId string reference — not the AdminUser aggregate itself.
 *
 * ─── CQRS Role ────────────────────────────────────────────────────────────────
 * WRITE side. Registration returns the generated adminId. Update returns void.
 *
 * Implementations:
 *   @see IOT.coldchain.application.handler.RegisterAdminHandler
 */
public interface RegisterAdminUseCase {

    /**
     * Registers a new AdminUser aggregate.
     *
     * Handler responsibilities:
     *   1. Build ContactInfo(command.email(), command.phoneNumber()) VO.
     *   2. Call AdminUserFactory.createNew(command.fullName(), contactInfo).
     *   3. Persist via AdminRepository.save(admin).
     *   4. Return the generated adminId.
     *
     * @param command encapsulates fullName, email, and phoneNumber
     * @return the generated UUID adminId of the newly registered admin
     * @throws IllegalArgumentException if fullName is blank or ContactInfo format is invalid
     */
    String handle(RegisterAdminCommand command);

    /**
     * Updates an AdminUser's contact information.
     *
     * Handler responsibilities:
     *   1. Load admin via AdminRepository.findById(command.adminId()).
     *   2. Build new ContactInfo(command.email(), command.phoneNumber()) VO.
     *   3. Call admin.updateContactInfo(newContactInfo).
     *   4. Persist via AdminRepository.save(admin).
     *
     * @param command encapsulates adminId, new email, and new phoneNumber
     * @throws IllegalArgumentException if admin not found, or contact format is invalid
     */
    void handle(UpdateAdminContactCommand command);
}