package org.mos.uaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "otp", ignoreUnknownFields = false)
public class OtpProperties {
    private long expiredTime;
    private long maxAttempt;
    private long resendDelay;
}
