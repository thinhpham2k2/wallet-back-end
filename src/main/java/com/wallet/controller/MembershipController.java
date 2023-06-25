package com.wallet.controller;

import com.wallet.dto.CustomerMembershipDTO;
import com.wallet.dto.MembershipDTO;
import com.wallet.dto.MembershipExtraDTO;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IMembershipService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Membership API")
@RequestMapping("/partner/api/members")
@SecurityRequirement(name = "Authorization")
public class MembershipController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IMembershipService membershipService;

    private final IJwtService jwtService;

    @GetMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Get membership list")
    public ResponseEntity<?> getAllMembership(@RequestParam(defaultValue = "") String search,

                                              @RequestParam(defaultValue = "") @Parameter(description = "<b>Filter by program ID<b>") List<Long> program,

                                              @RequestParam(defaultValue = "0") Optional<Integer> page,

                                              @RequestParam(defaultValue = "customer,desc") String sort,

                                              @RequestParam(defaultValue = "10") Optional<Integer> limit,
                                              HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        Page<MembershipDTO> memberships = membershipService.getMemberListForPartner(true, jwt, program, search, sort, page.orElse(0), limit.orElse(10));
        if (!memberships.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(memberships);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership list !");
        }
    }

    @GetMapping("/information")
    @Secured({PARTNER})
    @Operation(summary = "Get membership information")
    public ResponseEntity<?> getCustomerMembershipInform(@RequestParam(defaultValue = "") String customerId, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            CustomerMembershipDTO result = membershipService.getCustomerMembershipInform(jwt, customerId);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership inform !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @GetMapping("/{id}")
    @Secured({PARTNER})
    @Operation(summary = "Get membership detail")
    public ResponseEntity<?> getCustomerMembershipInform(@PathVariable(value = "id", required = false) Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            MembershipExtraDTO result = membershipService.getMemberById(jwt, id, false);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership detail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PostMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Create membership for customers that have been mapped into the wallet system")
    public ResponseEntity<?> createCustomerMembershipInform(@PathVariable(value = "id", required = false) Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            MembershipExtraDTO result = membershipService.getMemberById(jwt, id, false);
            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found membership detail !");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }
}
