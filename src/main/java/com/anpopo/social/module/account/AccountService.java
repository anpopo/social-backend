package com.anpopo.social.module.account;

import com.anpopo.social.infra.config.AppProperties;
import com.anpopo.social.infra.mail.EmailMessage;
import com.anpopo.social.infra.mail.EmailService;
import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.account.domain.Follow;
import com.anpopo.social.module.account.event.FollowRequestEvent;
import com.anpopo.social.module.account.event.FollowResponseEvent;
import com.anpopo.social.module.account.form.SignUpForm;
import com.anpopo.social.module.account.repository.AccountRepository;
import com.anpopo.social.module.account.repository.FollowRepository;
import com.anpopo.social.module.interest.Interest;
import com.anpopo.social.module.settings.form.NotificationForm;
import com.anpopo.social.module.settings.form.ProfileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final FollowRepository followRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void signUpProcess(SignUpForm signUpForm) {

        // ?????? ??????
        Account newAccount = createAndSaveUserAccount(signUpForm);

        // ?????? ??????
        sendEmail(newAccount);
        
        // ????????? ??????
        login(newAccount);

    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),  // spring security ?????? ???????????? principal
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        //????????? ???????????? ???????????? ?????????
        //- SecurityContext ??? Authentication(Token)??? ????????????????
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void sendEmail(Account account) {
        Context context = new Context();

        context.setVariable("link", "/email-check-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("message", "The Social ???????????? ???????????????.");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", "????????? ????????????");
        context.setVariable("nickname", account.getNickname());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("The Social, ?????? ?????? ??????")
                .message(message)
                .build();

        emailService.send(emailMessage);
    }

    public void completeSignUp(Account findAccount) {
        // ???????????? ?????? ?????? - ????????? ????????? true, ?????? ?????? ??????
        findAccount.completeSignUp();

        // ????????? ??????
        login(findAccount);
    }

    public void sendEmailLoginLink(Account account) {

        account.generateEmailCheckToken();

        Context context = new Context();

        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("message", "The Social ??? ????????? ????????? ????????? ???????????????");
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", "The Social ????????? ??????");
        context.setVariable("nickname", account.getNickname());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("The Social, ????????? ??????")
                .message(message)
                .build();

        emailService.send(emailMessage);

    }

    public void updateProfile(Account account, ProfileForm profileForm) {
        account.updateProfile(profileForm);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.updatePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationForm notificationForm) {
        account.updateNotifications(notificationForm);
        accountRepository.save(account);
    }

    private Account createAndSaveUserAccount(SignUpForm signUpForm) {
        // builder ?????? ????????? ???????????? null ??? ?????????.
        // ???????????? ?????? ???????????? ?????? ?????? ??? ???????????????@@@@@
        Account newAccount = new Account();
        newAccount.createNewAccount(
                signUpForm.getEmail(),
                signUpForm.getNickname(),
                passwordEncoder.encode(signUpForm.getPassword())
        );

        newAccount.generateEmailCheckToken();

        accountRepository.save(newAccount);
        return newAccount;
    }

    private SimpleMailMessage getSimpleMailMessage(Account account, String subject, String url) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(url + account.getEmailCheckToken() + "&email=" + account.getEmail());
        return mailMessage;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(emailOrNickname);

        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname + "??? ????????? ????????? ?????? ??? ????????????.");
        }

        return new UserAccount(account);
    }

    public void updateAccountInfo(Account account, String nickname) {
        account.updateAccountInfo(nickname);
        accountRepository.save(account);
        login(account);

    }

    public Set<Interest> getInterest(Account account) {
        Optional<Account> findAccount = accountRepository.findAccountWithInterestById(account.getId());
        return findAccount.orElseThrow(() -> new IllegalArgumentException("???????????? ?????? ?????? ?????? ?????????.")).getInterests();
    }

    public void addInterest(Account account, Interest interest) {
        Optional<Account> findAccount = accountRepository.findById(account.getId());
        findAccount.ifPresent(a -> {
            a.getInterests().add(interest);
            interest.addNumberOfAccount();
        });
    }

    public void removeInterest(Account account, Interest interest) {
        Optional<Account> findAccount = accountRepository.findById(account.getId());
        findAccount.ifPresent(a -> {
            a.getInterests().remove(interest);
            interest.minusNumberOfAccount();
        });
    }

    /**
     * findAccount -> ????????? ??? ?????? -> followed
     * account -> ???????????? ????????? ?????? -> follow
     */
    public void followRequest(Account findAccount, Account requestAccount) {
        // follow ????????? ?????? ????????? ????????? ????????? ??? ???????????? ????????? ????????? ????????? ?????? ??????
        if (!followRepository.existsFollowByFollowedAndFollow(findAccount, requestAccount)) {
            // ????????? follow ????????? ????????? ??????.
            Follow follow = followRepository.save( new Follow(findAccount, requestAccount));

            findAccount.addFollowers(follow);
            requestAccount.addFollowing(follow);

            eventPublisher.publishEvent(new FollowRequestEvent(follow));
        }
    }

    public void followCancel(Account findAccount, Account requestAccount) {
        Follow follow = followRepository.findFollowByFollowedAndFollow(findAccount, requestAccount);
        if (follow != null) {

            findAccount.deleteFollowers(follow);
            requestAccount.deleteFollowing(follow);
//            // ????????? follow ????????? ????????? ??????.
//            followRepository.delete(follow);
        }
    }
    public void followAccept(Account followAccount, Account requestAccount) {
        Follow follow = followRepository.findFollowByFollowedAndFollow(followAccount, requestAccount);
        if (follow != null) {
            follow.acceptFollowRequest();

            eventPublisher.publishEvent(new FollowResponseEvent(follow));
        }
    }

//    public void followReject(Account followAccount, Account requestAccount) {
//        Follow follow = followRepository.findFollowByFollowedAndFollow(followAccount, requestAccount);
//        if (follow != null) {
//            followAccount.deleteFollowers(follow);
//            requestAccount.deleteFollowing(follow);
//        }
//    }
}
