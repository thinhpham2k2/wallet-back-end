package com.wallet.controller;

import com.wallet.dto.*;
import com.wallet.entity.CustomUserDetails;
import com.wallet.service.interfaces.IJwtService;
import com.wallet.service.interfaces.IProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.security.InvalidParameterException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Program API")
@RequestMapping("/partner/api/programs")
@SecurityRequirement(name = "Authorization")
public class ProgramController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final IProgramService programService;

    private final IJwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @GetMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Get program list")
    public ResponseEntity<?> getAllProgram(@RequestParam(defaultValue = "") String search,

                                           @RequestParam(defaultValue = "0") Optional<Integer> page,

                                           @RequestParam(defaultValue = "programName,desc") String sort,

                                           @RequestParam(defaultValue = "10") Optional<Integer> limit, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        Page<ProgramDTO> program = programService.getProgramListForPartner(true, jwt, search, sort, page.orElse(0), limit.orElse(10));
        if (!program.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(program);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found program list !");
        }
    }

    @GetMapping("/{id}")
    @Secured({PARTNER})
    @Operation(summary = "Get program by id")
    public ResponseEntity<?> getProgramById(@PathVariable(value = "id", required = false) Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        ProgramExtraDTO program = programService.getProgramById(jwt, id, false);
        if (jwt != null) {
            if (program != null) {
                return ResponseEntity.status(HttpStatus.OK).body(program);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found program detail !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }


    @PostMapping("/token")
    @Operation(summary = "Get program token")
    public ResponseEntity<?> login(@RequestBody LoginFormDTO loginFormDTO) throws MethodArgumentTypeMismatchException {
        String userName = loginFormDTO.getUserName();
        String pass = loginFormDTO.getPassword();

        if (userName == null || userName.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing email");
        }

        if (pass == null || pass.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing password");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginFormDTO.getUserName(), loginFormDTO.getPassword()));
            CustomUserDetails partner = (CustomUserDetails) authentication.getPrincipal();
            if (partner.getPartner() != null) {
                String token = programService.getProgramTokenActiveByPartnerCode(partner.getPartner().getCode());
                if (token != null) {
                    return ResponseEntity.status(HttpStatus.OK).body(token);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found valid program active!");
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found valid program active!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user name or password");
        }
    }

    @PostMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Create program for partner")
    public ResponseEntity<?> createProgram(@RequestBody ProgramCreationDTO creation, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        ProgramExtraDTO program = programService.createProgram(creation, jwt);
        if (jwt != null) {
            if (program != null) {
                return ResponseEntity.status(HttpStatus.OK).body(program);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Create program fails !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PutMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Update program for partner")
    public ResponseEntity<?> updateProgram(@RequestBody ProgramUpdateDTO update, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            ProgramExtraDTO program = programService.updateProgram(update, jwt);
            if (program != null) {
                return ResponseEntity.status(HttpStatus.OK).body(program);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update program fails !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @PutMapping("/{id}")
    @Secured({PARTNER})
    @Operation(summary = "Update program state for partner")
    public ResponseEntity<?> updateProgramState(@PathVariable(value = "id") Long id, @RequestParam boolean state, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (jwt != null) {
            ProgramExtraDTO program = programService.updateProgramState(state, id, jwt);
            if (program != null) {
                return ResponseEntity.status(HttpStatus.OK).body(program);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update program fails !");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
    }

    @DeleteMapping("/{id}")
    @Secured({ADMIN})
    @Operation(summary = "Delete a program")
    public ResponseEntity<?> deleteProgram(@PathVariable(value = "id", required = false) Long id, HttpServletRequest request) throws MethodArgumentTypeMismatchException {
        String jwt = jwtService.getJwtFromRequest(request);
        if (id == null) {
            throw new InvalidParameterException("Invalid program Id");
        } else {
            if (jwt != null) {
                ProgramDTO programDTO = programService.deleteProgram(id, jwt);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(programDTO);

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found jwt token !");
            }
        }
    }
}
