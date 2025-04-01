package org.mos.uaa.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest extends RegisterRequest {

    private String oldPassword;
    private String password;
    private String confirmPassword;

}
