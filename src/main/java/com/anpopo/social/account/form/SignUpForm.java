package com.anpopo.social.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @Length(min = 5, max = 20, message = "길이 너무 짧거나 깁니다.")
    @NotBlank(message = "입력값이 비었습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-z0-9_-]{5,20}$", message = "한글, 영문, 숫자 및 _, - 만 가능합니다.")
    private String nickname;

    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "입력값이 비었습니다.")
    private String email;

    // TODO view에 비밀번호 보안등급 보여주기
    @NotBlank(message = "입력값이 비었습니다.")
    @Length(min=8, max=40, message = "8 ~ 40 자 사이입니다.")
    private String password;
    

}
