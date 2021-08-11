package com.anpopo.social.module.post.event;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.interest.Interest;
import lombok.Getter;

@Getter
public class FollowingInterestPostEvent {
    private Account account;
    private Interest interest;

    private Long id;

    public FollowingInterestPostEvent(Account account, Interest interest, Long id) {
        this.account = account;
        this.interest = interest;
        this.id = id;
    }
}
