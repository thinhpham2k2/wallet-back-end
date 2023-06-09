package com.wallet.controller;

import com.wallet.dto.*;
import com.wallet.service.interfaces.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin API")
@RequestMapping("/admin/api")
@SecurityRequirement(name = "Authorization")
public class AdminController {

    public static final String ADMIN = "ROLE_Admin";

    private final IAdminService adminService;

    private final IJwtService jwtService;

    private final IPartnerService partnerService;

    private final IProgramService programService;

    private final IMembershipService membershipService;

    private final ICustomerService customerService;

    @GetMapping("/customers")
    @Secured({ADMIN})
    @Operation(summary = "Get customer list")
    public ResponseEntity<?> getAllCustomer(@RequestParam(defaultValue = "") String search,

                                            @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by partner ID<b>") List<Long> partner,

                                            @RequestParam(defaultValue = "0") Optional<Integer> page,

                                            @RequestParam(defaultValue = "fullName,desc") String sort,

                                            @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<CustomerDTO> customer = customerService.getCustomerList(true, partner, search, sort, page.orElse(0), limit.orElse(10));
        if (!customer.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found customer list !");
        }
    }

    @GetMapping("/customers/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Get customer by detail")
    public ResponseEntity<?> getCustomerById(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
            CustomerExtraDTO result = customerService.getCustomerById("Admin token", id, true);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found customer detail !");
    }

    @GetMapping("/members")
    @Secured({ADMIN})
    @Operation(summary = "Get membership list")
    public ResponseEntity<?> getAllMembership(@RequestParam(defaultValue = "") String search,

                                              @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by partner ID<b>") List<Long> partner,

                                              @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by program ID<b>") List<Long> program,

                                              @RequestParam(defaultValue = "0") Optional<Integer> page,

                                              @RequestParam(defaultValue = "customer,desc") String sort,

                                              @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<MembershipDTO> memberships = membershipService.getMemberList(true, partner, program, search, sort, page.orElse(0), limit.orElse(10));
        if (!memberships.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(memberships);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership list !");
        }
    }

    @GetMapping("/members/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Get membership detail")
    public ResponseEntity<?> getCustomerMembershipInform(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        MembershipExtraDTO result = membershipService.getMemberById("Admin token", id, true);
        if (result != null) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership detail !");
    }

    @GetMapping("/programs")
    @Secured({ADMIN})
    @Operation(summary = "Get program list")
    public ResponseEntity<?> getAllProgram(@RequestParam(defaultValue = "") String search,

                                           @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by partner ID<b>") List<Long> partner,

                                           @RequestParam(defaultValue = "0") Optional<Integer> page,

                                           @RequestParam(defaultValue = "programName,desc") String sort,

                                           @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<ProgramDTO> program = programService.getProgramList(true, partner, search, sort, page.orElse(0), limit.orElse(10));
        if (!program.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(program);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found program list !");
        }
    }

    @GetMapping("/programs/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Get program by id")
    public ResponseEntity<?> getProgramById(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        ProgramExtraDTO program = programService.getProgramById("Admin token", id, true);
        if (program != null) {
            return ResponseEntity.status(HttpStatus.OK).body(program);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found program !");
        }
    }

    @DeleteMapping("/partners/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Delete a partner account")
    public ResponseEntity<?> deletePartner(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        if (id == null) {
            throw new InvalidParameterException("Invalid partner id");
        } else {
            PartnerDTO partner = partnerService.deletePartner(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(partner);
        }
    }

    @GetMapping("/partners/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Get partner by id")
    public ResponseEntity<?> getPartnerById(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        PartnerExtraDTO partner = partnerService.getPartnerExtra(id, true);
        if (partner != null) {
            return ResponseEntity.status(HttpStatus.OK).body(partner);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner !");
        }
    }

    @GetMapping("/partners")
    @Secured({ADMIN})
    @Operation(summary = "Get partner list")
    public ResponseEntity<?> getAllPartner(@RequestParam(defaultValue = "") String search,

                                           @RequestParam(defaultValue = "0") Optional<Integer> page,

                                           @RequestParam(defaultValue = "fullName,desc") String sort,

                                           @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<PartnerDTO> partners = partnerService.getPartnerList(true, search, sort, page.orElse(0), limit.orElse(10));
        if (!partners.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(partners);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner list !");
        }
    }

    @GetMapping("/admins/profile")
    @Secured({ADMIN})
    @Operation(summary = "Get admin profile")
    public ResponseEntity<?> getPartnerByUserName(HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            AdminDTO admin = adminService.getByUsernameAndStatus(jwt);
            if (admin != null) {
                return ResponseEntity.status(HttpStatus.OK).body(admin);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found admin profile !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @GetMapping("/admins/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Get admin by id")
    public ResponseEntity<?> getAdminById(@PathVariable(value = "id") Long id) throws MethodArgumentTypeMismatchException {
        AdminDTO adminDTO = adminService.getByIdAndStatus(id, true);
        if (adminDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(adminDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found admin !");
        }
    }

    @GetMapping("/admins")
    @Secured({ADMIN})
    @Operation(summary = "Get admin list")
    public ResponseEntity<?> getAllAdmin(@RequestParam(defaultValue = "") String search,

                                         @RequestParam(defaultValue = "0") Optional<Integer> page,

                                         @RequestParam(defaultValue = "fullName,desc") String sort,

                                         @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<AdminDTO> pageResult = adminService.getAdminList(true, search, sort, page.orElse(0), limit.orElse(10));
        if (!pageResult.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(pageResult);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found admin list !");
        }
    }

    @DeleteMapping("/admins/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Delete a admin account")
    public ResponseEntity<?> deleteAdmin(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        if (id == null) {
            throw new InvalidParameterException("Invalid partner id");
        } else {
            AdminDTO admin = adminService.deleteAdmin(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(admin);
        }
    }

    @PutMapping(value = "/admins/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({ADMIN})
    @Operation(summary = "Update a admin account")
    public ResponseEntity<?> updatePartner(@ModelAttribute AdminUpdateDTO adminDTO, @PathVariable(value = "id") Long id) throws MethodArgumentTypeMismatchException {
        AdminDTO admin = adminService.updateAdmin(adminDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    @PostMapping(value = "/admins", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({ADMIN})
    @Operation(summary = "Create a admin account")
    public ResponseEntity<?> registerPartner(@ModelAttribute AdminRegisterDTO adminRegisterDTO) throws MethodArgumentTypeMismatchException {
        JwtResponseDTO jwtResponseDTO = adminService.createAdmin(adminRegisterDTO, 17280000000L);
        if (jwtResponseDTO.getAdminDTO() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Register admin account failure !");
        }
    }
}
