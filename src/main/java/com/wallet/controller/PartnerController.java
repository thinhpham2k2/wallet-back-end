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

import java.security.InvalidParameterException;

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
    public ResponseEntity<?> getAllPartner(@RequestParam(required = false) Integer page) {
        Page<PartnerDTO> partners = partnerService.getAllPartner(true, page);
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
    @Operation(summary = "Register a partner account")
    public ResponseEntity<?> registerPartner(@RequestBody PartnerRegisterDTO partnerDTO) {
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
    public ResponseEntity<?> updatePartner(@RequestBody PartnerDTO partnerDTO, @PathVariable(value = "id") Long id) {
        PartnerDTO partner = partnerService.updatePartner(partnerDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(partner);
    }

    @DeleteMapping("/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Delete a partner account")
    public ResponseEntity<?> deletePartner(@PathVariable(value = "id", required = false) Long id) {
        if (id == null) {
            throw new InvalidParameterException("Invalid partner id");
        } else {
            PartnerDTO partner = partnerService.deletePartner(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(partner);
        }
    }
}
