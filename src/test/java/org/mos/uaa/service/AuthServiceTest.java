package org.mos.uaa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mos.uaa.entity.User;
import org.mos.uaa.kafka.KafkaProducer;
import org.mos.uaa.models.request.ChangePasswordRequest;
import org.mos.uaa.models.request.RegisterRequest;
import org.mos.uaa.models.response.ApiResponse;
import org.mos.uaa.redis.service.RegisterUserService;
import org.mos.uaa.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private RegisterUserService registerUserService;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private AuthService authService;

    @Test
    void register_withMinimumValidPhoneNumber_shouldPass() {
        RegisterRequest req = new RegisterRequest();
        req.setPhone("0912345678");

        when(userRepository.findByPhone("84912345678")).thenReturn(Optional.empty());
        when(otpService.generateOtp()).thenReturn("123456");
        doNothing().when(registerUserService).insertNew(anyString(), anyString(), anyString());
        doNothing().when(kafkaProducer).sendMessage(anyString(), any());

        ApiResponse<?> response = authService.register(req);

        assertEquals("OTP đã được gửi đến số điện thoại của bạn", response.getMessage());
    }

    @Test
    void changePassword_validInput_shouldSucceed() {
        ChangePasswordRequest req = new ChangePasswordRequest("oldPw", "newPw", "newPw");
        req.setPhone("84912345678");
        User user = new User("84912345678", "oldPw");

        when(userRepository.findByPhone("84912345678")).thenReturn(Optional.of(user));

        ApiResponse<?> response = authService.changePassword(req);

        assertEquals("Mật khẩu đã được thay đổi", response.getMessage());
    }

    @Test
    void changePassword_mismatchedConfirmPassword_shouldFail() {
        ChangePasswordRequest req = new ChangePasswordRequest("oldPw", "newPw", "newPw123");
        req.setPhone("84912345678");

        when(userRepository.findByPhone("84912345678")).thenReturn(Optional.of(new User()));

        ApiResponse<?> response = authService.changePassword(req);

        assertEquals("Mật khẩu không khớp", response.getMessage());
    }

    @Test
    void changePassword_userNotExists_shouldReturnError() {
        ChangePasswordRequest req = new ChangePasswordRequest("", "new", "new");
        req.setPhone("0912345678");

        when(userRepository.findByPhone("84912345678")).thenReturn(Optional.empty());

        ApiResponse<?> res = authService.changePassword(req);

        assertEquals("Số điện thoại không tồn tại", res.getMessage());
    }

    @Test
    void changePassword_whenConfirmPasswordDoesNotMatch_shouldReturnError() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setPhone("0912345678");
        req.setPassword("Newpass1!");
        req.setConfirmPassword("Mismatch");

        ApiResponse<?> response = authService.changePassword(req);

        assertEquals("Mật khẩu không khớp", response.getMessage());
    }

    @Test
    void changePassword_whenOldPasswordWrong_shouldReturnError() {
        User user = new User();
        user.setPhone("84912345678");
        user.setPassword("OldPass");
        user.setIsVerified(true);

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setPhone("0912345678");
        req.setOldPassword("WrongOldPass");
        req.setPassword("Newpass1!");
        req.setConfirmPassword("Newpass1!");

        when(userRepository.findByPhone("84912345678")).thenReturn(Optional.of(user));

        ApiResponse<?> res = authService.changePassword(req);

        assertEquals("Mật khẩu cũ không đúng", res.getMessage());
    }

    @Test
    void register_withExistingPhone_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setPhone("0912345678");

        when(userRepository.findByPhone("84912345678"))
                .thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Đã tồn tại tài khoản với số điện thoại này", ex.getMessage());
    }

}
