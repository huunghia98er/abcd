package org.mos.uaa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mos.uaa.config.OtpProperties;
import org.mos.uaa.models.request.BaseTransOtpRequest;
import org.mos.uaa.models.request.OtpValidationRequest;
import org.mos.uaa.redis.RegisterUserEntity;
import org.mos.uaa.redis.repository.RegisterUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    @Mock
    private RegisterUserRepository registerUserRepository;
    @Mock
    private OtpProperties otpProperties;
    @InjectMocks
    private OtpService otpService;

    @Test
    void resendOtp_justBeforeResendDelay_shouldThrow() {
        long now = System.currentTimeMillis();
        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setOtpResendTime(now - otpProperties.getResendDelay() + 1000);
        entity.setOtpResendCount(0);

        BaseTransOtpRequest req = new BaseTransOtpRequest();
        req.setTransactionId("txn123");

        when(registerUserRepository.findById("txn123")).thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class, () -> otpService.resendOtp(req));
    }

    @Test
    void resendOtp_maxRetry_shouldThrow() {
        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setOtpResendCount(otpProperties.getMaxAttempt());

        BaseTransOtpRequest req = new BaseTransOtpRequest();
        req.setTransactionId("txn456");

        when(registerUserRepository.findById("txn456")).thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class, () -> otpService.resendOtp(req));
    }

    @Test
    void validateOtp_multipleWrongAttempts_shouldBlockUser() {
        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setOtp("123456");
        entity.setOtpFail(5);

        OtpValidationRequest req = new OtpValidationRequest("000000");
        req.setTransactionId("txn789");

        when(registerUserRepository.findById("txn789")).thenReturn(Optional.of(entity));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> otpService.validateOtp(req));

        assertEquals("Vượt quá số lần nhập OTP", ex.getMessage());
    }

    @Test
    void validateOtp_whenWrongOtpAtMaxAttempts_shouldThrowAndDeleteSession() {
        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId("trans-1");
        entity.setOtp("123456");
        entity.setOtpFail(4);
        entity.setPhoneNumber("84912345678");

        OtpValidationRequest req = new OtpValidationRequest("000000");
        req.setTransactionId("trans-1");

        when(registerUserRepository.findById("trans-1")).thenReturn(Optional.of(entity));
        doNothing().when(registerUserRepository).deleteById("trans-1");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> otpService.validateOtp(req));

        assertEquals("Vượt quá số lần nhập OTP", ex.getMessage());
        verify(registerUserRepository).deleteById("trans-1");
    }

}
