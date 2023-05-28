package com.wallet.controller;

import com.wallet.dto.PartnerDTO;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getAllPartner(){
        Page<PartnerDTO> partners = partnerService.getAllPartner(true);
        if (!partners.getContent().isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(partners);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner list!");
        }
    }
}
