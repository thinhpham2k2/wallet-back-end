package com.wallet.controller;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.dto.PartnerUpdateDTO;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Partner API")
@RequestMapping("/partner/api/partners")
@SecurityRequirement(name = "Authorization")
public class PartnerController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IPartnerService partnerService;

    private final IJwtService jwtService;

    @GetMapping("/profile")
    @Secured({PARTNER})
    @Operation(summary = "Get partner profile")
    public ResponseEntity<?> getPartnerById(HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            PartnerDTO partner = partnerService.getPartnerProfile(jwt);
            if (partner != null) {
                return ResponseEntity.status(HttpStatus.OK).body(partner);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner profile !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("")
    @Operation(summary = "Register a partner account")
    public ResponseEntity<?> registerPartner(@RequestBody PartnerRegisterDTO partnerDTO) throws MethodArgumentTypeMismatchException {
        JwtResponseDTO jwtResponseDTO = partnerService.creatPartner(partnerDTO, 172800000L);
        if (jwtResponseDTO.getPartnerDTO() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Register partner account failure !");
        }
    }

    @PutMapping("/{id}")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Update a partner account")
    public ResponseEntity<?> updatePartner(@RequestBody PartnerUpdateDTO partnerDTO, @PathVariable(value = "id") Long id) throws MethodArgumentTypeMismatchException {
        PartnerUpdateDTO partner = partnerService.updatePartner(partnerDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(partner);
    }
}
