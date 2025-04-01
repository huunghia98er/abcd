package org.mos.uaa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.uaa.config.OtpProperties;
import org.mos.uaa.entity.User;
import org.mos.uaa.models.request.BaseTransOtpRequest;
import org.mos.uaa.models.request.OtpValidationRequest;
import org.mos.uaa.models.response.ApiResponse;
import org.mos.uaa.redis.RegisterUserEntity;
import org.mos.uaa.redis.repository.RegisterUserRepository;
import org.mos.uaa.redis.service.RegisterUserService;
import org.mos.uaa.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private final RegisterUserRepository registerUserRepository;
    private final RegisterUserService registerUserService;
    private final UserRepository userRepository;
    private final OtpProperties otpProperties;
    private final Random random;

    public String generateOtp() {
        return String.format("%06d", random.nextInt(999999));
    }

    public ApiResponse<?> resendOtp(BaseTransOtpRequest request) {
        Optional<RegisterUserEntity> registerUserEntity = this.registerUserRepository.findById(request.getTransactionId());

        if (registerUserEntity.isEmpty()) {
            throw new RuntimeException("Phiên làm việc không tồn tại hoặc đã hết hạn");
        }

        RegisterUserEntity entity = registerUserEntity.get();

        long currentTime = System.currentTimeMillis();
        if (currentTime - entity.getOtpResendTime() < otpProperties.getResendDelay()) {
            throw new RuntimeException("Vui lòng đợi " + otpProperties.getResendDelay() + " giây trước khi gửi lại OTP");
        }
        if (entity.getOtpResendCount() == otpProperties.getMaxAttempt()) {
            registerUserRepository.deleteById(entity.getTransactionId());
            throw new RuntimeException("Vượt quá số lần gửi lại OTP");
        }

        String otp = generateOtp();
        entity.setOtp(otp);

        registerUserService.renew(entity);

        return new ApiResponse<>("OTP đã được gửi lại", request.getTransactionId());
    }


    public void validateOtp(OtpValidationRequest request) {
        RegisterUserEntity entity = registerUserRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("OTP đã hết hạn hoặc không tồn tại"));

        if (!entity.getOtp().equals(request.getOtp())) {
            incrementFailedAttempts(entity);
            throw new RuntimeException("OTP không chính xác");
        }

        User user = new User();
        user.setPhone(entity.getPhoneNumber());
        user.setPassword(String.format("%06d", random.nextInt(1_000_000)));
        userRepository.save(user);

        registerUserRepository.deleteById(request.getTransactionId());
    }

    private void incrementFailedAttempts(RegisterUserEntity entity) {
        int attempts = entity.getOtpFail() + 1;
        if (attempts >= otpProperties.getMaxAttempt()) {
            registerUserRepository.deleteById(entity.getTransactionId());
            throw new RuntimeException("Vượt quá số lần nhập OTP");
        } else {
            entity.setOtpFail(attempts);
            registerUserRepository.save(entity);
        }
    }

}
