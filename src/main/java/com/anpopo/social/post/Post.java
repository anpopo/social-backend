package com.anpopo.social.post;

import com.anpopo.social.account.domain.Account;
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

    public Post(String context, Account account, String... postImages) {
        this.context = context;
        this.account = account;
        this.postedAt = LocalDateTime.now();

    }

    public void saveTags(Set<Tag> tags) {
        this.tags = tags;  // 1, 2, 3
    }

    public void updatePost(String context, Set<Tag> tags, String... postImages) {
        this.context = context;
        this.tags.clear();
        this.tags.addAll(tags);
        this.modifiedAt = LocalDateTime.now();

    }

    // TODO 댓글 기능 나중에 기릿




}
