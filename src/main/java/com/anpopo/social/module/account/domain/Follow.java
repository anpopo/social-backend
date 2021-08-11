package com.anpopo.social.module.account.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@NamedEntityGraph(
        name = "Follow.withFollowedAccount",
        attributeNodes = {@NamedAttributeNode("followed")}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private Account followed;

    // 요청 하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id")
    private Account follow;

    private boolean isAccepted = false;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;

    public Follow(Account followed, Account follow) {
        this.followed = followed;
        this.follow = follow;
        this.requestedAt = LocalDateTime.now();
    }

    public void acceptFollowRequest() {
        this.isAccepted = true;
        this.acceptedAt = LocalDateTime.now();
    }

}
