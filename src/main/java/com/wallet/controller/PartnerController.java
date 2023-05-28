package com.wallet.controller;

import com.wallet.dto.JwtResponseDTO;
import com.wallet.dto.PartnerDTO;
import com.wallet.dto.PartnerRegisterDTO;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Partner API")
@RequestMapping("/api/partners")
@SecurityRequirement(name = "Authorization")
public class PartnerController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IPartnerService partnerService;

    @GetMapping("")
    @Secured({ADMIN})
    @Operation(summary = "Get partner list")
    public ResponseEntity<?> getAllPartner() {
        Page<PartnerDTO> partners = partnerService.getAllPartner(true);
        if (!partners.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(partners);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner list !");
        }
    }

    @GetMapping("/{id}")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Get partner by id")
    public ResponseEntity<?> getPartnerById(@PathVariable(value = "id") Long id) {
        PartnerDTO partner = partnerService.getByIdAndStatus(id, true);
        if (partner != null) {
            return ResponseEntity.status(HttpStatus.OK).body(partner);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner !");
        }
    }

    @PostMapping("")
    @Operation(summary = "Create account partner")
    public ResponseEntity<?> createPartner(@RequestBody PartnerRegisterDTO partnerDTO) {
            JwtResponseDTO jwtResponseDTO = partnerService.creatPartner(partnerDTO, 172800000L);
            if (jwtResponseDTO.getPartnerDTO() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create partner account failure !");
            }
    }
}
