package com.wallet.controller;

import com.wallet.dto.*;
import com.wallet.service.interfaces.ICustomerService;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    private final IMembershipService membershipService;

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
    @Secured({PARTNER})
    @Operation(summary = "Get customer by detail")
    public ResponseEntity<?> getCustomerById(@PathVariable(value = "id", required = false) Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerExtraDTO result = customerService.getCustomerById(jwt, id, false);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found customer detail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("/membership")
    @Secured({PARTNER})
    @Operation(summary = "Create customer and membership")
    public ResponseEntity<?> createCustomerMembership(@RequestBody CustomerProgramDTO customer, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerMembershipDTO result = membershipService.createCustomerMembership(jwt, customer);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping(value = "/website/membership", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({PARTNER})
    @Operation(summary = "Create customer and membership in website")
    public ResponseEntity<?> createCustomerMembership(@ModelAttribute CustomerProgramWebDTO customer, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerMembershipDTO result = membershipService.createCustomerMembershipWeb(jwt, customer);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Create customer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerProgramDTO customer, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerMembershipDTO result = membershipService.createCustomer(jwt, customer);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping(value = "/website", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({PARTNER})
    @Operation(summary = "Create customer in website")
    public ResponseEntity<?> createCustomerWeb(@ModelAttribute CustomerProgramWebDTO customer, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerMembershipDTO result = membershipService.createCustomerWeb(jwt, customer);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PutMapping("/{id}")
    @Secured({PARTNER})
    @Operation(summary = "Update customer")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerUpdateDTO customerUpdateDTO, @PathVariable(value = "id") Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerExtraDTO result = customerService.updateCustomer(customerUpdateDTO, id, jwt);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @DeleteMapping("/{id}")
    @Secured({PARTNER})
    @Operation(summary = "Delete customer")
    public ResponseEntity<?> deleteCustomer(@PathVariable(value = "id") Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerExtraDTO result = customerService.deleteCustomer(id, jwt);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update customer fail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }
}
