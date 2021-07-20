package com.anpopo.social.account;

import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.mail.ConsoleMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final ConsoleMailSender mailSender;

    @Transactional
    public void signUpProcess(SignUpForm signUpForm) {

        Account newAccount = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .build();

        // 회원 저장
        accountRepository.save(newAccount);

        // 메일 발송
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("The Social, 회원 가입 인증");
        // TODO 토큰 설정 하기
        mailMessage.setText("/");



    }
}
