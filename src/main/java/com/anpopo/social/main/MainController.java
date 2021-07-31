package com.anpopo.social.main;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(@CurrentUser Account account) {
        if(account != null) {
            return "redirect:/";
        }

        return "login";
    }
}
