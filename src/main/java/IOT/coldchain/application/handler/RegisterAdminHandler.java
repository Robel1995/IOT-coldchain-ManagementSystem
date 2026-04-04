package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.command.RegisterAdminCommand;
import IOT.coldchain.application.dto.command.UpdateAdminContactCommand;
import IOT.coldchain.application.port.in.command.RegisterAdminUseCase;
import IOT.coldchain.application.port1.out.AdminRepository;
import IOT.coldchain.domain.entity.AdminUser;
import IOT.coldchain.domain.entity.AdminUserFactory;
import IOT.coldchain.domain.valueobject.ContactInfo;

/**
 * COMMAND HANDLER — RegisterAdminHandler
 *
 * Handles all operations related to AdminUser aggregate management:
 * registration and contact updates.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the RegisterAdminUseCase inbound port and depends
 * on the AdminRepository outbound port. It contains NO framework annotations.
 *
 * ─── Error Handling ───────────────────────────────────────────────────────────
 * All exceptions from the domain are propagated unchanged to the caller.
 */
public class RegisterAdminHandler implements RegisterAdminUseCase {

    private final AdminRepository adminRepository;

    public RegisterAdminHandler(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public String handle(RegisterAdminCommand command) {
        ContactInfo contactInfo = new ContactInfo(command.email(), command.phoneNumber());

        AdminUser admin = AdminUserFactory.createNew(command.fullName(), contactInfo);

        AdminUser savedAdmin = adminRepository.save(admin);
        return savedAdmin.getAdminId();
    }

    @Override
    public void handle(UpdateAdminContactCommand command) {
        AdminUser admin = adminRepository.findById(command.adminId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Admin not found with ID: " + command.adminId()));

        ContactInfo newContactInfo = new ContactInfo(command.email(), command.phoneNumber());
        admin.updateContactInfo(newContactInfo);

        adminRepository.save(admin);
    }
}
