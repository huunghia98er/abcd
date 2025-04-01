package org.mos.uaa.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Pattern(regexp = "^(\\+84|0|84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

}
