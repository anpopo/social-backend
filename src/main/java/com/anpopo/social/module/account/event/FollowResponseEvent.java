package com.anpopo.social.module.account.event;

import com.anpopo.social.module.account.domain.Follow;
import lombok.Getter;

@Getter
public class FollowResponseEvent {

    private Follow follow;

    public FollowResponseEvent(Follow follow) {
        this.follow = follow;
    }
}
