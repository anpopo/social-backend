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

    public void saveTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void updatePost(String context, Set<Tag> tags) {
        this.context = context;
        this.tags.clear();
        this.tags = tags;
    }

    public void createNewPost(String context, Account account) {
        this.context = context;
        this.account = account;
    }

    // TODO 댓글 기능 나중에 기릿




}
