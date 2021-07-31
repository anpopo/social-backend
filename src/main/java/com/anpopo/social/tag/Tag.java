package com.anpopo.social.tag;

import com.anpopo.social.account.domain.Account;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Builder @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 40)
    @Column(nullable = false, unique = true)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime createdAt;
}
