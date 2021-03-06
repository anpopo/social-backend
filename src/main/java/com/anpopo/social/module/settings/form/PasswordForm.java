package com.anpopo.social.module.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor
public class PasswordForm {

    @NotBlank(message = "입력값이 비었습니다.")
    @Length(min=8, max=40, message = "8 ~ 40 자 사이로 입력하셔야 합니다.")
    private String newPassword;

    @NotBlank(message = "입력값이 비었습니다.")
    @Length(min=8, max=40, message = "8 ~ 40 자 사이로 입력하셔야 합니다.")
    private String newPasswordConfirm;
}
