package com.wallet.controller;

import com.wallet.dto.CustomerDTO;
import com.wallet.dto.CustomerExtraDTO;
import com.wallet.service.interfaces.ICustomerService;
import com.wallet.service.interfaces.IJwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Customer API")
@RequestMapping("/partner/api/customers")
@SecurityRequirement(name = "Authorization")
public class CustomerController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final ICustomerService customerService;

    private final IJwtService jwtService;

    @GetMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Get customer list")
    public ResponseEntity<?> getAllCustomer(@RequestParam(defaultValue = "") String search,

                                            @RequestParam(defaultValue = "0") Optional<Integer> page,

                                            @RequestParam(defaultValue = "fullName,desc") String sort,

                                            @RequestParam(defaultValue = "10") Optional<Integer> limit, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        Page<CustomerDTO> customer = customerService.getCustomerListForPartner(true, jwt, search, sort, page.orElse(0), limit.orElse(10));
        if (!customer.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found customer list !");
        }
    }

    @GetMapping("/{id}")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Get customer by customer Id")
    public ResponseEntity<?> getCustomerById(@PathVariable(value = "id", required = false) Long id) throws MethodArgumentTypeMismatchException {
        CustomerExtraDTO customerExtra = customerService.getCustomerById(true, id);
        if (customerExtra != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customerExtra);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found customer !");
        }
    }
}
