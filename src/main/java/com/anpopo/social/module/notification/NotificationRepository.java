package com.anpopo.social.module.notification;

import com.anpopo.social.module.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountAndCheckedOrderByCreatedAtDesc(Account account, boolean checked);

    List<Notification> findByAccountAndChecked(Account account, boolean b);

    List<Notification> findByCreatedAtBefore(LocalDateTime minusDays);
}
