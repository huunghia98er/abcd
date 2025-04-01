package org.mos.uaa.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

    public static class RedisKey {
        public static final String OTP = "OTP_";
        public static final String OTP_SEND_TIME_ = "OTP_SEND_TIME_";
        public static final String OTP_ATTEMPT = "OTP_ATTEMPT_";
    }

    public static class KafkaTopic {
        public static final String OTP = "otp-topic";
    }

    public static class Message {
        public static final String OTP_RESEND_DELAY = "Vui lòng đợi %s giây trước khi gửi lại OTP";
        public static final String OTP_EXPIRATION = "OTP đã hết hạn";
        public static final String OTP_NOT_AVALIBLE = "OTP không hợp lệ hoặc đã hết hạn";
    }

}
