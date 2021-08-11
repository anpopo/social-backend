package com.anpopo.social.module.account.event;

import com.anpopo.social.module.account.domain.Follow;
import lombok.Getter;

@Getter
public class FollowRequestEvent {

    private Follow follow;

    public FollowRequestEvent(Follow follow) {
        this.follow = follow;
    }
}
