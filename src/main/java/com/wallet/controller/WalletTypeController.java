package com.wallet.controller;

import com.wallet.service.interfaces.IWalletTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
