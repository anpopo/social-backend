package com.anpopo.social.account;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.account.repository.AccountRepository;
import com.anpopo.social.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @InitBinder("signUpForm")
    public void signUpFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(@CurrentUser Account account, Model model) {
        if (account != null) {
            return "redirect:/";
        }

        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid SignUpForm signUpForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(signUpForm);
            return "account/sign-up";
        }

        accountService.signUpProcess(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/email-check-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account findAccount = accountRepository.findByEmail(email);

        // email 검증
        if ( findAccount == null) {
            model.addAttribute("error", "wrong.email");
            return "account/checked-email";
        }

        // token 값 검증
        if ( !findAccount.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return "account/checked-email";
        }

        accountService.completeSignUp(findAccount);

        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", findAccount.getNickname());
        model.addAttribute(findAccount);
        return "account/checked-email";
    }

    @GetMapping("/check-email")
    public String checkEmailForm(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        model.addAttribute(account);
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendEmail(@CurrentUser Account account, Model model) {
        if (!account.canSendConfirmMail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());

            return "account/check-email";
        }

        accountService.sendMail(account);

        return "redirect:/";
    }

    @GetMapping("/email-login")
    public String emailLoginView(@CurrentUser Account account) {
        if (account != null) {
            return "redirect:/";
        }

        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String emailLogin(String email, Model model, RedirectAttributes redirectAttributes) {
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            model.addAttribute("error", "해당 이메일로 가입된 계정을 찾을 수 없습니다.");
            return "account/email-login";
        }
        // 이메일을 무차별로 보낼 수 있기 때문에 1시간에 1건을 할 수 있도록 해야한다.

        if (!account.canSendConfirmMail()) {
            model.addAttribute("error", "이메일은 1시간에 1번만 보낼 수 있습니다.");
            return "account/email-login";
        }

        // 이메일로 로그인 가능한 링크 보내기
        accountService.sendEmailLoginLink(account);
        redirectAttributes.addFlashAttribute("message", "인증 이메일을 발송 했습니다.");

        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String emailLoginCheckToken(String token, String email, Model model) {

        Account findAccount = accountRepository.findByEmail(email);

        if (findAccount == null) {
            model.addAttribute("error", "로그인에 실패했습니다.");
            return "account/email-logged-in";
        }

        if (!findAccount.isValidToken(token)) {
            model.addAttribute("error", "로그인에 실패했습니다.");
            return "account/email-logged-in";
        }

        accountService.login(findAccount);
        model.addAttribute(findAccount);

        return "account/email-logged-in";
    }

    @GetMapping("/profile/@{nickname}")
    public String viewProfile(@PathVariable String nickname, @CurrentUser Account account, Model model) {
        Account findAccount = accountRepository.findAccountWithFollowersAndFollowingByNickname(nickname);

        if (findAccount == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute("account", account);
        model.addAttribute( "findAccount", findAccount);
        model.addAttribute("isOwner", findAccount.equals(account));
        model.addAttribute("isFollowing", false);

        return "account/profile";
    }

    /**
     * findAccount -> 팔로우 할 계정
     * requestAccount -> 팔로우를 신청한 계정
     * requestAccount -> findAccount 에게 팔로우를 요청한 상황
     */
    @PostMapping("/{nickname}/follow/request")
    public String followRequest(@CurrentUser Account account, @PathVariable String nickname, RedirectAttributes redirectAttributes) {

        Account findAccount = accountRepository.findAccountWithFollowersByNickname(nickname);

        // 유효한 닉네임인지 검사
        if (findAccount == null || findAccount.equals(account)) {
            throw new IllegalArgumentException("올바른 팔로우 요청이 아닙니다");
        }

        Account requestAccount = accountRepository.findAccountWithFollowingById(account.getId());

        accountService.followRequest(findAccount, requestAccount);

        return "redirect:/profile/@" + findAccount.getEncodedNickname();

    }

    @PostMapping("/follow/accept")
    public String followAccept(@CurrentUser Account account, String nickname) {
        // userA -> userB 에게 팔로우를 요청한 상황에서
        // userB 수락을 누른 경우
        // account -> userB 수락을 누른 로그인 한 계졍
        // nickname -> userA 요청을 신청한 계정 닉네임

        Account requestAccount = accountRepository.findAccountWithFollowingByNickname(nickname);
        if (requestAccount == null || requestAccount.equals(account)) {
            throw new IllegalArgumentException("팔로우 수락 요청에 문제가 있습니다.");
        }

        Account followAccount = accountRepository.findAccountWithFollowersById(account.getId());

        accountService.followAccept(followAccount, requestAccount);

        return "redirect:/settings/followers";
    }

    @PostMapping("/follow/reject")
    public String followReject(@CurrentUser Account account, String nickname) {

        Account requestAccount = accountRepository.findAccountWithFollowingByNickname(nickname);
        if (requestAccount == null || requestAccount.equals(account)) {
            throw new IllegalArgumentException("팔로우 거절 요청에 문제가 있습니다.");
        }

        Account followAccount = accountRepository.findAccountWithFollowersById(account.getId());

        accountService.followReject(followAccount, requestAccount);
        return "redirect:/settings/followers";
    }
}
