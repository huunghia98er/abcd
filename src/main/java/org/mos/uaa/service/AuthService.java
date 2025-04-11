package org.mos.uaa.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.uaa.constant.Constant;
import org.mos.uaa.entity.User;
import org.mos.uaa.kafka.KafkaProducer;
import org.mos.uaa.kafka.OtpKafka;
import org.mos.uaa.models.request.ChangePasswordRequest;
import org.mos.uaa.models.request.RegisterRequest;
import org.mos.uaa.models.response.ApiResponse;
import org.mos.uaa.redis.service.RegisterUserService;
import org.mos.uaa.repository.UserRepository;
import org.mos.uaa.utils.PhoneNumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RegisterUserService registerUserService;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final KafkaProducer kafkaProducer;

    public ApiResponse<?> register(@RequestBody @Valid RegisterRequest request) {
        String phone = PhoneNumberUtils.normalizePhoneNumber(request.getPhone());

        this.userRepository.findByPhone(phone)
                .ifPresent(user -> {
                    throw new RuntimeException("Đã tồn tại tài khoản với số điện thoại này");
                });

        String transactionId = UUID.randomUUID().toString();

        String otp = this.otpService.generateOtp();

        this.registerUserService.insertNew(transactionId, phone, otp);

        this.kafkaProducer.sendMessage(Constant.KafkaTopic.OTP, new OtpKafka(otp, phone));

        return new ApiResponse<>("OTP đã được gửi đến số điện thoại của bạn", transactionId);
    }

    public ApiResponse<?> changePassword(ChangePasswordRequest request) {
        String phone = PhoneNumberUtils.normalizePhoneNumber(request.getPhone());

        Optional<User> userOptional = this.userRepository.findByPhone(phone);

        if (userOptional.isEmpty()) {
            return new ApiResponse<>("Số điện thoại không tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new ApiResponse<>("Mật khẩu không khớp");
        }

        User user = userOptional.get();

        if (user.getIsVerified() && !user.getPassword().equals(request.getOldPassword())) {
            return new ApiResponse<>("Mật khẩu cũ không đúng");
        }

        user.setPassword(request.getPassword());
        user.setIsVerified(true);
        user.setIsRequiredChangePw(false);
        this.userRepository.save(user);

        return new ApiResponse<>("Mật khẩu đã được thay đổi");
    }

}
