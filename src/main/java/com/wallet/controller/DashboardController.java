package com.wallet.controller;

import com.wallet.service.interfaces.ICustomerService;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "Dashboard API")
@RequestMapping("/partner/api/dashboards")
@SecurityRequirement(name = "Authorization")
public class DashboardController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final ICustomerService customerService;

    private final ITransactionService transactionService;

    private final IJwtService jwtService;

    @GetMapping("/titles")
    @Secured({PARTNER})
    @Operation(summary = "Get title for dashboard")
    public ResponseEntity<?> getTitle(HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customerService.getTitle(jwt));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @GetMapping("/charts")
    @Secured({PARTNER})
    @Operation(summary = "Get chart for dashboard")
    public ResponseEntity<?> getChart(@RequestParam(defaultValue = "") LocalDate fromDate,
                                      @RequestParam(defaultValue = "") LocalDate toDate,
                                      @RequestParam(defaultValue = "") String sort,
                                      HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            if (fromDate == null || toDate == null) {
                fromDate = LocalDate.now();
                toDate = LocalDate.now();
            }
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionByPartner(fromDate, toDate, sort, jwt));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }
}
