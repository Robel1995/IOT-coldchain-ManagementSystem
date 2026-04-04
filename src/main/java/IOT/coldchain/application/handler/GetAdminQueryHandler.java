package IOT.coldchain.application.handler;

import IOT.coldchain.application.dto.query.GetAdminByIdQuery;
import IOT.coldchain.application.dto.query.GetAllAdminsQuery;
import IOT.coldchain.application.dto.view.AdminDetailView;
import IOT.coldchain.application.dto.view.AdminSummaryView;
import IOT.coldchain.application.port.in.query.GetAdminQueryUseCase;
import IOT.coldchain.application.port1.out.AdminRepository;
import IOT.coldchain.domain.entity.AdminUser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY HANDLER — GetAdminQueryHandler
 *
 * Handles all read operations for AdminUser aggregates.
 *
 * ─── CQRS Read Side ───────────────────────────────────────────────────────────
 * This handler is read-only. It returns flat view objects and never mutates
 * domain aggregates.
 *
 * ─── Clean Architecture ───────────────────────────────────────────────────────
 * This handler implements the GetAdminQueryUseCase inbound port and depends
 * on the AdminRepository outbound port. It contains NO framework annotations.
 */
public class GetAdminQueryHandler implements GetAdminQueryUseCase {

    private final AdminRepository adminRepository;

    public GetAdminQueryHandler(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public AdminDetailView handle(GetAdminByIdQuery query) {
        AdminUser admin = adminRepository.findById(query.adminId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Admin not found with ID: " + query.adminId()));

        return new AdminDetailView(
                admin.getAdminId(),
                admin.getFullName(),
                admin.getContactInfo().getEmail(),
                admin.getContactInfo().getPhoneNumber()
        );
    }

    @Override
    public List<AdminSummaryView> handle(GetAllAdminsQuery query) {
        List<AdminUser> admins = adminRepository.findAll();

        return admins.stream()
                .skip((long) query.page() * query.size())
                .limit(query.size())
                .map(admin -> new AdminSummaryView(
                        admin.getAdminId(),
                        admin.getFullName(),
                        admin.getContactInfo().getEmail()
                ))
                .collect(Collectors.toList());
    }
}
