package com.anpopo.social.module.main;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.account.CurrentUser;
import com.anpopo.social.module.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final NotificationRepository notificationRepository;

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
