package com.wallet.controller;

import com.wallet.dto.MembershipDTO;
import com.wallet.service.interfaces.IMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Membership API")
@RequestMapping("/api/members")
@SecurityRequirement(name = "Authorization")
public class MembershipController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IMembershipService membershipService;

    @GetMapping("")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Get membership list")
    public ResponseEntity<?> getAllCustomer(@RequestParam(defaultValue = "") String search,

                                            @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by partner ID<b>") List<Long> partner,

                                            @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by program ID<b>") List<Long> program,

                                            @RequestParam(defaultValue = "0") Optional<Integer> page,

                                            @RequestParam(defaultValue = "customer,desc") String sort,

                                            @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<MembershipDTO> memberships = membershipService.getMemberList(true, partner, program, search, sort, page.orElse(0), limit.orElse(10));
        if (!memberships.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(memberships);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership list !");
        }
    }
}
