package org.mos.uaa.kafka;

import lombok.extern.slf4j.Slf4j;
import org.mos.uaa.constant.Constant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OtpConsumer {

    @KafkaListener(topics = Constant.KafkaTopic.OTP, groupId = "otp-group")
    public void consumeMessage(OtpKafka message) {
        log.info("Received message: {}", message.toString());
    }

}
