package com.anpopo.social.module.post.event;

import com.anpopo.social.module.account.domain.Account;
import lombok.Getter;

@Getter
public class FollowingPostEvent {
    private Account account;
    private Long id;

    public FollowingPostEvent(Account account, Long id) {
        this.account = account;
        this.id = id;
    }
}
