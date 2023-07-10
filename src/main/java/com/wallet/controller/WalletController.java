package com.wallet.controller;

import com.wallet.dto.WalletDTO;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IWalletService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Wallet API")
@RequestMapping("/partner/api/wallets")
@SecurityRequirement(name = "Authorization")
public class WalletController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IWalletService walletService;

    private final IJwtService jwtService;

    @GetMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Get wallet list by program token")
    public ResponseEntity<?> findAllByProgramTokenAndCustomerId(@RequestParam(defaultValue = "") String customerId, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            List<WalletDTO> result = walletService.findAllByProgramTokenAndCustomerId(jwt, customerId);
            if (!result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found wallet list !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Create wallet by program token")
    public ResponseEntity<?> createWallet(@RequestParam(defaultValue = "0") long membershipId, @RequestParam(defaultValue = "0") long typeId, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            WalletDTO result = walletService.createWallet(jwt, membershipId, typeId);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found wallet !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }
}
