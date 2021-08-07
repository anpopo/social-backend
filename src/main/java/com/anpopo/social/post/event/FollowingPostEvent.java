package com.anpopo.social.post.event;

import com.anpopo.social.account.domain.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowingPostEvent {
    private Account account;
    private Long id;

    public FollowingPostEvent(Account account, Long id) {
        this.account = account;
        this.id = id;
    }
}
