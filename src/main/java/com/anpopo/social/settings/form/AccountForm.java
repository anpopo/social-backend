package com.anpopo.social.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data @NoArgsConstructor
public class AccountForm {

    @Length(min = 5, max = 20, message = "길이 너무 짧거나 깁니다.")
    @NotBlank(message = "입력값이 비었습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-z0-9._-]{5,20}$", message = "한글, 영문, 숫자 및 . _ - 만 가능합니다.")
    private String nickname;
}
