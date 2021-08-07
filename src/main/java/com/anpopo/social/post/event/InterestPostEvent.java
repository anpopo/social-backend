package com.anpopo.social.post.event;

import com.anpopo.social.interest.Interest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InterestPostEvent{

    private Interest interest;
    private Long id;

    public InterestPostEvent(Interest interest, Long id) {
        this.interest = interest;
        this.id = id;
    }
}
