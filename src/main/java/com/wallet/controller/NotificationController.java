package com.wallet.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.wallet.dto.NoteDTO;
import com.wallet.service.interfaces.IFirebaseMessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification API")
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "Authorization")
public class NotificationController {

    public static final String PARTNER = "ROLE_Partner";

    private final IFirebaseMessagingService firebaseService;

    @PostMapping("")
    @Secured({PARTNER})
    @Operation(summary = "Push notification")
    public ResponseEntity<?> getAllLevel(@RequestBody NoteDTO note,
                                         @RequestParam String token) throws MethodArgumentTypeMismatchException, FirebaseMessagingException {
        return ResponseEntity.status(HttpStatus.OK).body(firebaseService.sendNotification(note, token));
    }
}
