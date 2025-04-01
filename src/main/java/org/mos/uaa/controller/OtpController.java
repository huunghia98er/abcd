package org.mos.uaa.controller;

import lombok.RequiredArgsConstructor;
import org.mos.uaa.models.request.OtpValidationRequest;
import org.mos.uaa.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(OtpValidationRequest request) {
        return this.otpService.validate(request);
    }

}
