package com.anpopo.social.settings;

import com.anpopo.social.account.AccountRepository;
import com.anpopo.social.account.AccountService;
import com.anpopo.social.account.CurrentUser;
import com.anpopo.social.domain.Account;
import com.anpopo.social.domain.Tag;
import com.anpopo.social.settings.form.*;
import com.anpopo.social.settings.validator.AccountFormValidator;
import com.anpopo.social.settings.validator.PasswordFormValidator;
import com.anpopo.social.tag.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/settings")
@Controller
public class SettingsController {

    private final AccountService accountService;
    private final PasswordFormValidator passwordValidator;
    private final AccountFormValidator accountFormValidator;
    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;
    private final AccountRepository accountRepository;

    @InitBinder("passwordForm")
    public void initPasswordValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordValidator);
    }

    @InitBinder("accountForm")
    public void initAccountValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountFormValidator);
    }

    @GetMapping("/profile")
    public String updateProfileView(@CurrentUser Account account, Model model) {

        ProfileForm profileForm = new ProfileForm();

        profileForm.setNickname(account.getNickname());
        profileForm.setBio(account.getBio());
        profileForm.setProfileImage(account.getProfileImage());

        model.addAttribute(profileForm);

        // header fragment 에서 사용하는 account 객체 등록해야 함.
        model.addAttribute(account);

        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@CurrentUser Account account, @Valid ProfileForm profileForm, Errors errors, Model model, RedirectAttributes redirectAttributes) {

        log.info("this is the text: {}", profileForm.getBio());
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(profileForm);
            return "settings/profile";
        }

        accountService.updateProfile(account, profileForm);
        redirectAttributes.addFlashAttribute("message", "프로필 수정을 완료했습니다.");

        return "redirect:/settings/profile";
    }

    @GetMapping("/password")
    public String updatePasswordView(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(passwordForm);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        redirectAttributes.addFlashAttribute("message", "비밀번호를 변경했습니다.");

        return "redirect:/settings/password";
    }

    @GetMapping("/notifications")
    public String updateNotificationsView(@CurrentUser Account account, Model model) {
        model.addAttribute(account);

        NotificationForm notificationForm = new NotificationForm();
        notificationForm.setFollowingAccountPostingByWeb(account.isFollowingAccountPostingByWeb());
        notificationForm.setFavoriteSubjectPostingByWeb(account.isFavoriteSubjectPostingByWeb());

        model.addAttribute(notificationForm);

        return "settings/notifications";
    }

    @PostMapping("/notifications")
    public String updateNotifications(@CurrentUser Account account, @Valid NotificationForm notificationForm, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(notificationForm);
            return "settings/notifications";
        }

        accountService.updateNotifications(account, notificationForm);
        redirectAttributes.addFlashAttribute("message", "알람 설정을 변경했습니다.");

        return "redirect:/settings/notifications";
    }

    @GetMapping("/account")
    public String updateAccountInfoView(@CurrentUser Account account, Model model) {
        model.addAttribute(account);

        AccountForm accountForm = new AccountForm();
        accountForm.setNickname(account.getNickname());

        model.addAttribute(accountForm);
        return "settings/account";
    }

    @PostMapping("/account")
    public String updateAccountInfo(@CurrentUser Account account, @Valid AccountForm accountForm, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(accountForm);
            return "settings/account";
        }

        accountService.updateAccountInfo(account, accountForm.getNickname());
        redirectAttributes.addFlashAttribute("message", "계정 정보를 변경했습니다.");

        return "redirect:/settings/account";
    }

    @GetMapping("/tags")
    public String updateTagsView(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Tag> tags = accountService.getTag(account);

        model.addAttribute("tags", tags.stream().map(Tag::getTitle).sorted().collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute("whiteList", objectMapper.writeValueAsString(allTags));

        return "settings/tags";
    }

    @ResponseBody
    @PostMapping("/tags/add")
    public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        accountService.addAccountTag(account, title);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Optional<Tag> tag = tagRepository.findByTitle(title);

        if (tag.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag.get());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers")
    public String followersView(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Account> followers = accountService.getFollowers(account);

        model.addAttribute(
                "followers",
                followers.stream()
                    .map(a -> new FollowForm(a.getId(), a.getNickname(), a.getProfileImage()))
                    .collect(Collectors.toList()));

        model.addAttribute("numberOfFollowers", followers.size());

        return "settings/followers";
    }

    @GetMapping("/followings")
    public String followingsView(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Account> followings = accountService.getFollowings(account);

        model.addAttribute(
                "followings",
                followings.stream()
                        .map(a -> new FollowForm(a.getId(), a.getNickname(), a.getProfileImage()))
                        .collect(Collectors.toList()));

        model.addAttribute("numberOfFollowings", followings.size());

        return "settings/followings";
    }

}
