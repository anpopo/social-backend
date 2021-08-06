package com.anpopo.social.account.event;

import com.anpopo.social.account.domain.Follow;
import lombok.Data;
import lombok.Getter;

@Getter
public class FollowRequestEvent {

    private Follow follow;

    public FollowRequestEvent(Follow follow) {
        this.follow = follow;
    }
}
