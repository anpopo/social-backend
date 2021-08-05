package com.anpopo.social.settings;

import com.anpopo.social.account.repository.AccountRepository;
import com.anpopo.social.account.AccountService;
import com.anpopo.social.account.CurrentUser;
import com.anpopo.social.account.domain.Account;
import com.anpopo.social.interest.Interest;
import com.anpopo.social.interest.InterestRepository;
import com.anpopo.social.settings.form.*;
import com.anpopo.social.settings.validator.AccountFormValidator;
import com.anpopo.social.settings.validator.PasswordFormValidator;
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
import java.util.ArrayList;
import java.util.List;
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
    private final AccountRepository accountRepository;
    private final InterestRepository interestRepository;

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
        notificationForm.setInterestSubjectPostingByWeb(account.isInterestSubjectPostingByWeb());

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

    @GetMapping("/interest")
    public String updateInterestView(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Interest> interests = accountService.getInterest(account);

        model.addAttribute("interests", interests.stream().map(Interest::getInterest).collect(Collectors.toList()));

        List<String> allInterest = interestRepository.findAll().stream()
                .map(Interest::getInterest)
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("whiteList", objectMapper.writeValueAsString(allInterest));

        return "settings/interest";
    }

    @ResponseBody
    @PostMapping("/interest/add")
    public ResponseEntity addInterest(@CurrentUser Account account, @RequestBody InterestForm interestForm) {
        Interest interest = interestRepository.findByInterest(interestForm.getInterest());
        if (interest == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addInterest(account, interest);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/interest/remove")
    public ResponseEntity removeInterest(@CurrentUser Account account, @RequestBody InterestForm interestForm) {
        Interest interest = interestRepository.findByInterest(interestForm.getInterest());
        if (interest == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeInterest(account, interest);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/followers")
    public String followerView(@CurrentUser Account account, Model model) {

        Account findAccount = accountRepository.findAccountWithFollowersWithAccountById(account.getId());
        model.addAttribute(findAccount);

        List<FollowForm> followers = new ArrayList<>();
        List<FollowForm> followersPending = new ArrayList<>();

        findAccount.getFollowers()
                .forEach(f -> {
                    FollowForm followForm = new FollowForm();
                    followForm.setId(f.getFollow().getId());
                    followForm.setNickname(f.getFollow().getNickname());
                    followForm.setProfileImage(f.getFollow().getProfileImage());
                    followForm.setAccepted(f.isAccepted());

                    if(f.isAccepted()) followers.add(followForm);
                    else followersPending.add(followForm);
                });

        model.addAttribute("followers",followers);
        model.addAttribute("followersPending",followersPending);

        return "settings/followers";
    }

    @GetMapping("/following")
    public String followingView(@CurrentUser Account account, Model model) {
        Account findAccount = accountRepository.findAccountWithFollowingWithAccountById(account.getId());
        model.addAttribute(findAccount);

        List<FollowForm> following = new ArrayList<>();
        List<FollowForm> followingPending = new ArrayList<>();

        findAccount.getFollowing()
                .forEach(f -> {
                    FollowForm followForm = new FollowForm();
                    followForm.setId(f.getFollowed().getId());
                    followForm.setNickname(f.getFollowed().getNickname());
                    followForm.setProfileImage(f.getFollowed().getProfileImage());
                    followForm.setAccepted(f.isAccepted());

                    if(f.isAccepted()) following.add(followForm);
                    else followingPending.add(followForm);
                });

        model.addAttribute("following",following);
        model.addAttribute("followingPending",followingPending);

        for (FollowForm followForm : followingPending) {
            System.out.println("followForm = " + followForm);
        }

        return "settings/following";
    }
}
