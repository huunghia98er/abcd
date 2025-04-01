package org.mos.uaa.message_queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {

    @KafkaListener(topics = "otp-topic", groupId = "otp-group")
    public void consumeMessage(String message) {
        log.info(message);
    }

}
