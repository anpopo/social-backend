package com.anpopo.social.post;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.interest.Interest;
import com.anpopo.social.tag.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private Account account;

    private LocalDateTime postedAt;

    private LocalDateTime modifiedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags;

    @Lob
    private String postImage1;

    @Lob
    private String postImage2;

    @Lob
    private String postImage3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;

    public Post(String context, Account account, Interest interest, String... postImages) {
        this.context = context;
        this.account = account;
        this.interest = interest;
        this.postedAt = LocalDateTime.now();

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

    // TODO 댓글 기능 나중에 기릿




}
