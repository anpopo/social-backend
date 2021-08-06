package com.anpopo.social.notification;

import com.anpopo.social.account.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.EntityGraph;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String link;
    private String message;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime createdAt;
    // 팔로워 작성글 알람
    // 관심주제 작성글 알람
    // 팔로우 요청
    // 팔로우 수락
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
