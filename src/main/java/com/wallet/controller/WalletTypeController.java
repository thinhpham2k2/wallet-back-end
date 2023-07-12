package com.wallet.controller;

import com.wallet.service.interfaces.IWalletTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Wallet type API")
@RequestMapping("/partner/api/wallet-types")
@SecurityRequirement(name = "Authorization")
public class WalletTypeController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IWalletTypeService walletTypeService;

    @GetMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Get wallet type")
    public ResponseEntity<?> getAllType() {
        return ResponseEntity.status(HttpStatus.OK).body(walletTypeService.getAllType());
    }
}
