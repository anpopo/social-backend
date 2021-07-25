package com.anpopo.social.account;

import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.domain.Account;
import com.anpopo.social.settings.form.ProfileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public void signUpProcess(SignUpForm signUpForm) {

        // 회원 저장
        Account newAccount = createAndSaveUserAccount(signUpForm);

        newAccount.generateEmailCheckToken();

        // 메일 발송
        sendMail(newAccount);
        
        // 로그인 처리
        login(newAccount);
    }

    private void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),  // spring security 에서 참조하는 principal
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        //스프링 스큐리티 관점에서 로그인
        //- SecurityContext 에 Authentication(Token)이 존재하는가?
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void sendMail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("The Social, 회원 가입 인증");
        mailMessage.setText("/email-check-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        mailSender.send(mailMessage);
    }

    private Account createAndSaveUserAccount(SignUpForm signUpForm) {
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

    public void completeSignUp(Account findAccount) {
        // 회원가입 완료 처리 - 이메일 인증값 true, 회원 가입 시간
        findAccount.completeSignUp();

        // 로그인 처리
        login(findAccount);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(emailOrNickname);

        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname + "로 등록된 정보를 찾을 수 없습니다.");
        }

        return new UserAccount(account);
    }


    public void updateProfile(Account account, ProfileForm profileForm) {
        account.updateProfile(profileForm);
        accountRepository.save(account);
    }
}
