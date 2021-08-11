package com.anpopo.social.module.account.event;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.account.domain.Follow;
import com.anpopo.social.module.notification.Notification;
import com.anpopo.social.module.notification.NotificationRepository;
import com.anpopo.social.module.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Transactional
@Async
@RequiredArgsConstructor
@Component
public class FollowEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleFollowRequestEvent(FollowRequestEvent followRequestEvent) {
        Follow follow = followRequestEvent.getFollow();
        // follow 요청한 사람에게 보내는 알림
        Notification notificationToFollow = createNotification("팔로우 요청 알림", "/settings/following",
                "'" + follow.getFollowed().getNickname() + "'님에게 팔로우 요청을 보냈습니다",
                follow.getFollow(), NotificationType.FOLLOW_REQUEST);


        // 팔로우 요청을 받는 사람에게 보내는 알림
        Notification notificationToFollowed = createNotification("팔로우 요청 알림", "/settings/followers",
                "'" + follow.getFollow().getNickname() + "'님에게 팔로우 요청이 왔습니다.",
                follow.getFollowed(), NotificationType.FOLLOW_REQUEST);


        notificationRepository.save(notificationToFollow);
        notificationRepository.save(notificationToFollowed);
    }

    @EventListener
    public void handleFollowResponseEvent(FollowResponseEvent followResponseEvent) {
        Follow follow = followResponseEvent.getFollow();

        Notification notification = createNotification("팔로우 수락 알림", "/profile/@" + follow.getFollowed().getEncodedNickname(),
                "'" + follow.getFollowed().getNickname() + "'님이 팔로우 요청을 수락하셨습니다.",
                follow.getFollow(), NotificationType.FOLLOW_RESPONSE);

        notificationRepository.save(notification);
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
