package com.anpopo.social.account;

import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.mail.ConsoleMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUpProcess(SignUpForm signUpForm) {

        // 회원 저장
        Account newAccount = saveAccount(signUpForm);

        newAccount.generateEmailCheckToken();

        // 메일 발송
        sendMail(newAccount);

        // TODO 로그인 처리


    }

    private void sendMail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("The Social, 회원 가입 인증");
        // TODO 토큰 설정 하기
        mailMessage.setText("/email-check-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
        mailSender.send(mailMessage);
    }

    private Account saveAccount(SignUpForm signUpForm) {
        Account newAccount = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .favoriteSubjectPostingByWeb(false)
                .favoriteUserPostingByWeb(false)
                .build();

        accountRepository.save(newAccount);
        return newAccount;
    }
}
