package com.anpopo.social.account.event;

import com.anpopo.social.account.domain.Follow;
import lombok.Data;
import lombok.Getter;

@Getter
public class FollowResponseEvent {

    private Follow follow;

    public FollowResponseEvent(Follow follow) {
        this.follow = follow;
    }
}
