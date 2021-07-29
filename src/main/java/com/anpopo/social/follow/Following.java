package com.anpopo.social.follow;

import com.anpopo.social.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Following {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime requestAt;

    private LocalDateTime acceptedAt;

    private boolean isAccepted;

}
