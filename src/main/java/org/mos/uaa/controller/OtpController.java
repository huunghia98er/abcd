package org.mos.uaa.controller;

import lombok.RequiredArgsConstructor;
import org.mos.uaa.models.request.BaseTransOtpRequest;
import org.mos.uaa.models.request.OtpValidationRequest;
import org.mos.uaa.models.response.ApiResponse;
import org.mos.uaa.service.OtpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/resend-otp")
    public ApiResponse<?> resendOtp(@RequestBody BaseTransOtpRequest request) {
        return this.otpService.resendOtp(request);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<?> verifyOtp(OtpValidationRequest request) {
        this.otpService.validateOtp(request);
        return new ApiResponse<>("Yêu cầu thực hiện thành công");
    }

}
