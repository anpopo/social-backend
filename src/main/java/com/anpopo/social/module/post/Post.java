package com.anpopo.social.module.post;

import com.anpopo.social.module.account.UserAccount;
import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.interest.Interest;
import com.anpopo.social.module.tag.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Post {

    @Id @GeneratedValue
    private Long id;

    @Lob
    private String context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime postedAt;

    private LocalDateTime modifiedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;

    @Lob
    private String postImage1;

    @Lob
    private String postImage2;

    @Lob
    private String postImage3;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> likeAccount = new HashSet<>();

    @Column(nullable = false, columnDefinition = "default 0")
    private Integer likeCount = 0;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    public Integer getLikeCount() {
        return this.likeAccount.size();
    }

    public Post(String context, Account account, Interest interest, String... postImages) {
        this.context = context;
        this.account = account;
        this.interest = interest;
        this.postedAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();

        postImage1 = postImages[0];
        postImage2 = postImages[1];
        postImage3 = postImages[2];
    }

    public void saveTags(Set<Tag> tags) {
        this.tags = tags;  // 1, 2, 3
    }

    public void updatePost(String context, Set<Tag> tags, Interest interest, String... postImages) {
        this.context = context;
        if (!this.interest.equals(interest)) {
            this.interest = interest;
            interest.minusNumberOfPost();
        }
        this.tags.clear();
        this.tags.addAll(tags);
        this.modifiedAt = LocalDateTime.now();

        this.postImage1 = postImages[0];
        this.postImage2 = postImages[1];
        this.postImage3 = postImages[2];
    }

    public void addLike(Account account) {
        if (!likeAccount.contains(account)) {
            this.likeAccount.add(account);
            this.likeCount += 1;
        }
    }

    public void minusLike(Account account) {
        if (this.likeAccount.contains(account)) {
            this.likeAccount.remove(account);

            if (this.likeCount - 1 <= 0) {
                this.likeCount = 0;
            }  else {
                this.likeCount -= 1;
            }
        }
    }

    public boolean isLike(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.likeAccount.contains(account);
    }

    // TODO 댓글 기능 나중에 기릿

    public boolean isWriter(UserAccount userAccount) {
        return this.account.equals(userAccount.getAccount());
    }


}
