package com.anpopo.social.account;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;
    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean cellPhoneVerified;

    private String cellPhoneCheckToken;

    private LocalDateTime cellPhoneCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;

    // TODO 관심사 등록하기
    // TODO 관심 있는 유저

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 관심 있는 유저가 포스팅을 한 경우 알람 설정
    private boolean favoriteUserPostingByWeb;

    // 관심 있는 주제로 포스팅이 올라온 경우 알람 설정
    private boolean favoriteSubjectPostingByWeb;

}
