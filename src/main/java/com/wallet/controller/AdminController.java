package com.wallet.controller;

import com.wallet.dto.AdminDTO;
import com.wallet.dto.AdminRegisterDTO;
import com.wallet.dto.AdminUpdateDTO;
import com.wallet.dto.JwtResponseDTO;
import com.wallet.service.interfaces.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.security.InvalidParameterException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin API")
@RequestMapping("/api/admins")
@SecurityRequirement(name = "Authorization")
public class AdminController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IAdminService adminService;

    @GetMapping("{id}")
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

    @GetMapping("")
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

    @DeleteMapping("/{id}")
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

    @PutMapping("/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Update a admin account")
    public ResponseEntity<?> updatePartner(@RequestBody AdminUpdateDTO adminDTO, @PathVariable(value = "id") Long id) throws MethodArgumentTypeMismatchException {
        AdminDTO admin = adminService.updateAdmin(adminDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    @PostMapping("")
    @Operation(summary = "Create a admin account")
    public ResponseEntity<?> registerPartner(@RequestBody AdminRegisterDTO adminRegisterDTO) throws MethodArgumentTypeMismatchException {
        JwtResponseDTO jwtResponseDTO = adminService.createAdmin(adminRegisterDTO, 17280000000L);
        if (jwtResponseDTO.getAdminDTO() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Register admin account failure !");
        }
    }
}
