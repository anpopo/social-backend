package com.anpopo.social.module.notification;

import com.anpopo.social.module.account.CurrentUser;
import com.anpopo.social.module.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/notifications")
@Controller
public class NotificationController {
    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;


    @GetMapping
    public String getNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, false);
        long count = notificationRepository.countByAccountAndChecked(account, true);
        putCategorizedNotifications(model, notifications, count, notifications.size());
        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "notification/list";

    }


    @GetMapping("/old")
    public String getOldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, true);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, false);

        putCategorizedNotifications(model, notifications, notifications.size(), numberOfChecked);

        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @PostMapping("/delete")
    public String deleteNotifications(@CurrentUser Account account) {
        List<Notification> notifications = notificationRepository.findByAccountAndChecked(account, true);

        notificationRepository.deleteAll(notifications);

        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<Notification> followNotifications = new ArrayList<>();
        List<Notification> followingPostingNotifications = new ArrayList<>();
        List<Notification> interestPostingNotifications = new ArrayList<>();
        List<Notification> etc = new ArrayList<>();

        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case FOLLOW_REQUEST:
                case FOLLOW_RESPONSE: followNotifications.add(notification); break;
                case FOLLOWING_POST: followingPostingNotifications.add(notification); break;
                case INTEREST_POST: interestPostingNotifications.add(notification); break;
                case ETC: etc.add(notification); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("followNotifications", followNotifications);
        model.addAttribute("followingPostingNotifications", followingPostingNotifications);
        model.addAttribute("interestPostingNotifications", interestPostingNotifications);
        model.addAttribute("etc", etc);

    }

}
