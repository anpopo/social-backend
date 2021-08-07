package com.anpopo.social.post.event;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.domain.Follow;
import com.anpopo.social.account.repository.AccountRepository;
import com.anpopo.social.account.repository.FollowRepository;
import com.anpopo.social.interest.Interest;
import com.anpopo.social.interest.InterestRepository;
import com.anpopo.social.notification.Notification;
import com.anpopo.social.notification.NotificationRepository;
import com.anpopo.social.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Transactional
@Async
@RequiredArgsConstructor
@Component
public class PostEventListener {

    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @EventListener
    public void handleFollowingPostEvent(FollowingPostEvent followingPostEvent) {
        Account account = followingPostEvent.getAccount();

        // 해당 아이디를 팔로잉으로 등록한 계정에게 알람 보내기
        List<Follow> followList = followRepository.findByFollowed(account);
        Set<Notification> saveList = new HashSet<>();
        if (followList != null) {
            followList.forEach(follow -> {
                Notification notification = createNotification("팔로우 포스팅 알림", "/post/" + followingPostEvent.getId(),
                        "'" + account.getNickname() + "'님이 포스팅을 하셨습니다.",
                        follow.getFollow(), NotificationType.FOLLOWING_POST);

                saveList.add(notification);
            });

            notificationRepository.saveAll(saveList);
        }
    }

    @EventListener
    public void handleInterestPostEvent(InterestPostEvent interestPostEvent) {
        Interest interest = interestPostEvent.getInterest();

        List<Account> accounts = accountRepository.findAccountByInterest(interest);
        Set<Notification> saveList = new HashSet<>();

        if (accounts != null) {
            accounts.forEach(account -> {
                Notification notification = createNotification("관심주제 포스팅 알림", "/post/" + interestPostEvent.getId(),
                        "'" + interest.getInterest() + "'주제로 포스팅이 되었습니다.",
                        account, NotificationType.INTEREST_POST);

                saveList.add(notification);
            });
            notificationRepository.saveAll(saveList);
        }


    }

    private Notification createNotification(String title, String link, String message, Account account, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setLink(link);
        notification.setChecked(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);

        return notification;
    }
}
