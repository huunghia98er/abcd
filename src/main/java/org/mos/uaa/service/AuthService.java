package org.mos.uaa.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.uaa.constant.Constant;
import org.mos.uaa.entity.User;
import org.mos.uaa.message_queue.Producer;
import org.mos.uaa.models.request.ChangePasswordRequest;
import org.mos.uaa.models.request.RegisterRequest;
import org.mos.uaa.repository.UserRepository;
import org.mos.uaa.utils.PhoneNumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final Producer producer;

    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        String phone = PhoneNumberUtils.normalizePhoneNumber(request.getPhone());
        Optional<User> existingUser = this.userRepository.findByPhone(phone);

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại");
        }

        String otp = this.otpService.generateOtp(phone);

        this.producer.sendMessage(Constant.KafkaTopic.OTP, otp);

        return ResponseEntity.ok("OTP đã được gửi");
    }

    public ResponseEntity<?> changePassword(ChangePasswordRequest request) {
        String phone = PhoneNumberUtils.normalizePhoneNumber(request.getPhone());
        Optional<User> userOptional = this.userRepository.findByPhone(phone);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Số điện thoại không tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu không khớp");
        }

        User user = userOptional.get();

        if (user.getIsVerified() && !user.getPassword().equals(request.getOldPassword())) {
            return ResponseEntity.ok().body("Mật khẩu cũ không đúng");
        }

        user.setPassword(request.getPassword());
        user.setIsVerified(true);
        this.userRepository.save(user);

        return ResponseEntity.ok("Mật khẩu đã được thay đổi");
    }

}
