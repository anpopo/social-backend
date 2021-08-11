package com.anpopo.social.module.post.event;

import com.anpopo.social.module.interest.Interest;
import lombok.Getter;

@Getter
public class InterestPostEvent{

    private Interest interest;
    private Long id;

    public InterestPostEvent(Interest interest, Long id) {
        this.interest = interest;
        this.id = id;
    }
}
