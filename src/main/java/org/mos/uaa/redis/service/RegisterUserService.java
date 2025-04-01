package org.mos.uaa.redis.service;

import lombok.RequiredArgsConstructor;
import org.mos.uaa.config.OtpProperties;
import org.mos.uaa.redis.RegisterUserEntity;
import org.mos.uaa.redis.repository.RegisterUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserService {
    private final RegisterUserRepository registerUserRepository;
    private final OtpProperties otpProperties;

    public void insertNew(String transactionId, String phone, String otp) {
        RegisterUserEntity entity = new RegisterUserEntity();
        entity.setTransactionId(transactionId);
        entity.setPhoneNumber(phone);
        entity.setOtp(otp);
        entity.setOtpFail(0);
        entity.setOtpResendCount(0);
        entity.setOtpResendTime(System.currentTimeMillis());
        entity.setOtpExpiredTime(System.currentTimeMillis() + otpProperties.getExpiredTime() * 1000);
        entity.setTtl(otpProperties.getExpiredTime());
        registerUserRepository.save(entity);
    }

    public void renew(RegisterUserEntity entity) {
        entity.setOtpResendCount(entity.getOtpResendCount() + 1);
        entity.setOtpResendTime(System.currentTimeMillis());
        entity.setOtpExpiredTime(System.currentTimeMillis() + otpProperties.getExpiredTime() * 1000);
        entity.setTtl(otpProperties.getExpiredTime());
        entity.setOtpFail(0);
        registerUserRepository.save(entity);
    }

}
