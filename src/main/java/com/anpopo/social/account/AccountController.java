package com.anpopo.social.account;

import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.account.validator.SignUpFormValidator;
import com.anpopo.social.domain.Account;
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

    @GetMapping("/profile/@{nickname}")
    public String viewProfile(@PathVariable String nickname, @CurrentUser Account account, Model model) {
        Account byNickname = accountRepository.findByNickname(nickname);

        if (byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        boolean isFollowing = accountService.getFollowingsContain(account, byNickname);

        model.addAttribute("account", account);
        model.addAttribute( "findAccount", byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        model.addAttribute("isFollowing", isFollowing);

        return "account/profile";
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

    @GetMapping("/following/{nickname}/request")
    public String followingRequest(@CurrentUser Account account, @PathVariable String nickname, Model model) {
        Account findAccount = accountRepository.findAccountWithFollowersByNickname(nickname);

        accountVerified(findAccount);

        selfFollowCheck(account, findAccount);

        accountService.requestFollowing(account.getId(), findAccount);

        return "redirect:/profile/@" + findAccount.getEncodedNickname();
    }

    @GetMapping("/following/{nickname}/delete")
    public String followingDelete(@CurrentUser Account account, @PathVariable String nickname, Model model) {
        Account findAccount = accountRepository.findAccountWithFollowersByNickname(nickname);

        accountVerified(findAccount);

        selfFollowCheck(account, findAccount);

        accountService.deleteFollowing(account.getId(), findAccount);

        return "redirect:/profile/@" + findAccount.getEncodedNickname();
    }

    @GetMapping("/following/settings/{nickname}/delete")
    public String followingDeleteFromSettings(@CurrentUser Account account, @PathVariable String nickname, Model model) {
        Account findAccount = accountRepository.findAccountWithFollowersByNickname(nickname);

        accountVerified(findAccount);

        selfFollowCheck(account, findAccount);

        accountService.deleteFollowing(account.getId(), findAccount);

        return "redirect:/settings/followings";
    }

    private void accountVerified(Account findAccount) {
        if (findAccount == null) {
            throw new IllegalArgumentException("해당하는 유저가 존재하지 않습니다.");
        }
    }

    private void selfFollowCheck(@CurrentUser Account account, Account findAccount) {
        if (account.equals(findAccount)) {
            throw new IllegalArgumentException("자신에게 팔로우를 신청할 수 없습니다.");
        }
    }

}
