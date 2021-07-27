package com.anpopo.social.domain;

import com.anpopo.social.settings.form.NotificationForm;
import com.anpopo.social.settings.form.ProfileForm;
import lombok.*;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NamedEntityGraph(name = "Account.withFollowings", attributeNodes = {
        @NamedAttributeNode("followings")
})
@NamedEntityGraph(name = "Account.withFollowers", attributeNodes = {
        @NamedAttributeNode("followers")
})


@Getter @Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column(length = 1000)
    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean cellPhoneVerified;

    private String cellPhoneCheckToken;

    private LocalDateTime cellPhoneCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private Set<AccountTag> accountTags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> followings = new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY)
//    private Set<Account> followingRequestPending = new HashSet<>();
//
//    @OneToMany(fetch = FetchType.LAZY)
//    private Set<Account> followingRecievePending = new HashSet<>();

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    // 팔로잉 한 계정의 포스팅이 올라온 경우의 알람 설정
    private boolean followingAccountPostingByWeb;

    // 관심 있는 주제로 포스팅이 올라온 경우 알람 설정
    private boolean favoriteSubjectPostingByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean canSendConfirmMail() {
        // 제일 처음 재전송 메일을 보내는 경우
        if(this.emailCheckTokenGeneratedAt == null) {
            this.emailCheckTokenGeneratedAt = LocalDateTime.now();
            return true;
        }
        // 재전송 메일을 두번째 이상 보내는 경우 -> 시간 확인이 필요
        else {
            // 1시간 안에 보낸 메일인가?
            if (this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1))){
                this.emailCheckTokenGeneratedAt = LocalDateTime.now();
                return true;
            }
            return false;
        }
    }

    public void updateProfile(ProfileForm profileForm) {
        this.bio = profileForm.getBio();
        this.profileImage = profileForm.getProfileImage();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateNotifications(NotificationForm notificationForm) {
        this.followingAccountPostingByWeb = notificationForm.isFollowingAccountPostingByWeb();
        this.favoriteSubjectPostingByWeb = notificationForm.isFavoriteSubjectPostingByWeb();
    }

    public void updateAccountInfo(String nickname) {
        this.nickname = nickname;
    }
}
