package com.anpopo.social.follow;

import com.anpopo.social.account.domain.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.jdo.annotations.Join;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private Account followed;

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
}
