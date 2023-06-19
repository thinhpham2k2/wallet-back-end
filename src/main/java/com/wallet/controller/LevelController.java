package com.wallet.controller;

import com.wallet.dto.LevelDTO;
import com.wallet.service.interfaces.ILevelService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Level API")
@RequestMapping("/api/levels")
@SecurityRequirement(name = "Authorization")
public class LevelController {

    public static final String ADMIN = "ROLE_Admin";

    public static final String PARTNER = "ROLE_Partner";

    private final ILevelService levelService;

    @GetMapping("")
    @Secured({ADMIN, PARTNER})
    @Operation(summary = "Get level list")
    public ResponseEntity<?> getAllLevel(@RequestParam(defaultValue = "0") Optional<Integer> page,

                                         @RequestParam(defaultValue = "condition,asc") String sort,

                                         @RequestParam(defaultValue = "10") Optional<Integer> limit) throws MethodArgumentTypeMismatchException {
        Page<LevelDTO> pageResult = levelService.getLevelList(sort, page.orElse(0), limit.orElse(10));
        if (!pageResult.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(pageResult);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found level list !");
        }
    }
}
