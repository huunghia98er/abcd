package org.mos.uaa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mos.uaa.models.request.ChangePasswordRequest;
import org.mos.uaa.models.request.RegisterRequest;
import org.mos.uaa.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(this.authService.register(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(this.authService.changePassword(request));
    }

}
