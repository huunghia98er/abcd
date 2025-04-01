package org.mos.uaa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mos.uaa.constant.Constant;
import org.mos.uaa.entity.User;
import org.mos.uaa.models.request.OtpValidationRequest;
import org.mos.uaa.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    private static final int OTP_EXPIRED_TIME = 180;
    private static final int OTP_RESEND_DELAY = 120;
    private static final int OTP_MAX_ATTEMPTS = 5;

    public String generateOtp(String phone) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(Constant.RedisKey.OTP + phone, otp, Duration.ofSeconds(OTP_EXPIRED_TIME));
        redisTemplate.opsForValue().set(Constant.RedisKey.OTP_ATTEMPT + phone, "0", Duration.ofSeconds(OTP_EXPIRED_TIME));
        redisTemplate.opsForValue().set(Constant.RedisKey.OTP_SEND_TIME_ + phone, LocalDateTime.now().toString(), Duration.ofSeconds(OTP_EXPIRED_TIME));
        return otp;
    }

    public ResponseEntity<?> validate(OtpValidationRequest request) {
        String phone = request.getPhone().replace("+84", "84").replaceFirst("^0", "84");

        if (!this.validateOtp(phone, request.getOtp())) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn");
        }

        User user = new User();
        user.setPhone(phone);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    private boolean validateOtp(String phone, String otp) {
        String storedOtp = String.valueOf(redisTemplate.opsForValue().get(Constant.RedisKey.OTP + phone));
        if (storedOtp == null || !storedOtp.equals(otp)) {
            incrementFailedAttempts(phone);
            return false;
        }
        redisTemplate.delete(Constant.RedisKey.OTP + phone);
        return true;
    }

    private void incrementFailedAttempts(String phone) {
        String key = Constant.RedisKey.OTP_ATTEMPT + phone;

        Object attemptsObj = redisTemplate.opsForValue().get(key);
        Object sendTimeObj = redisTemplate.opsForValue().get(Constant.RedisKey.OTP_SEND_TIME_ + phone);

        if (Objects.isNull(attemptsObj) || Objects.isNull(sendTimeObj)) {
            throw new RuntimeException(Constant.Message.OTP_EXPIRATION);
        }

        LocalDateTime sendTime = LocalDateTime.parse(String.valueOf(sendTimeObj));

        if (Duration.between(sendTime, LocalDateTime.now()).toSeconds() < OTP_RESEND_DELAY) {
            throw new RuntimeException(String.format(Constant.Message.OTP_RESEND_DELAY, OTP_MAX_ATTEMPTS));
        }

        int attempts = Integer.parseInt(String.valueOf(attemptsObj));

        if (attempts >= OTP_MAX_ATTEMPTS) {
            redisTemplate.delete(Constant.RedisKey.OTP + phone);
            redisTemplate.delete(key);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
    }

}
