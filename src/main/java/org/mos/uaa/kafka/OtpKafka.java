package org.mos.uaa.kafka;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OtpKafka implements Serializable {
    private String otp;
    private String phoneNumber;
}
