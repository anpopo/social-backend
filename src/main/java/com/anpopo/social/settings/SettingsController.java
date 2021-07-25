package com.anpopo.social.settings;

import com.anpopo.social.account.AccountService;
import com.anpopo.social.account.CurrentUser;
import com.anpopo.social.domain.Account;
import com.anpopo.social.settings.form.ProfileForm;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/settings")
@Controller
public class SettingsController {

    private final AccountService accountService;

    @GetMapping("/profile")
    public String updateProfileView (@CurrentUser Account account, Model model) {

        ProfileForm profileForm = new ProfileForm();

        profileForm.setNickname(account.getNickname());
        profileForm.setBio(account.getBio());
        profileForm.setProfileImage(account.getProfileImage());
        model.addAttribute(profileForm);

        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@CurrentUser Account account, ProfileForm profileForm, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(profileForm);
            model.addAttribute(account);

            return "settings/profile";
        }

        accountService.updateProfile(account, profileForm);
        redirectAttributes.addFlashAttribute("message", "프로필 수정을 완료했습니다.");

        return "redirect:/settings/profile";
    }

}
