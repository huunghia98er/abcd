package org.mos.uaa.models.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest extends RegisterRequest {

    private String oldPassword;
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~`|\\\\]).{8,}$",
            message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ, số, chữ hoa và ký tự đặc biệt"
    )
    private String password;
    private String confirmPassword;

}
