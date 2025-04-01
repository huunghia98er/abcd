package org.mos.uaa.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpValidationRequest extends RegisterRequest {

    private String otp;

}
