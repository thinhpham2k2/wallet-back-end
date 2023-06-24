package com.wallet.controller;

import com.wallet.dto.RequestSubtractionDTO;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Request API")
@RequestMapping("/partner/api/requests")
@SecurityRequirement(name = "Authorization")
public class RequestController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IRequestService requestService;

    private final IJwtService jwtService;

    @PostMapping("/token")
    @Secured({PARTNER})
    @Operation(summary = "Create a request to reduce wallet balance !")
    public ResponseEntity<?> createRequestSubtraction(@RequestBody RequestSubtractionDTO subtraction, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            if (subtraction != null) {

                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid request !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }
}
