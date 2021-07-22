package com.anpopo.social.account;

import com.anpopo.social.account.form.SignUpForm;
import com.anpopo.social.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String signUpForm(Model model) {
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

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account findAccount = accountRepository.findByEmail(email);

        String view = "account/checked-email";

        if ( findAccount == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if ( !findAccount.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }


        accountService.completeSignUp(findAccount);

        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", findAccount.getNickname());
        return view;
    }
}
