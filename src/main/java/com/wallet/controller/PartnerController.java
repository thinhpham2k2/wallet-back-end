package com.wallet.controller;

import com.wallet.dto.PartnerDTO;
import com.wallet.service.interfaces.IPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Partner API")
@RequestMapping("/api/partner")
@SecurityRequirement(name = "Authorization")
@Slf4j
public class PartnerController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IPartnerService partnerService;

    @GetMapping("")
    @Secured({ADMIN})
    @Operation(summary = "Get all partner")
    public ResponseEntity<?> getAllPartner(){
        List<PartnerDTO> partners = partnerService.getAllPartner(true);
        if (!partners.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(partners);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found partner list!");
        }
    }
}
