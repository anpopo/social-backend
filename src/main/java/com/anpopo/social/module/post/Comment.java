package com.anpopo.social.module.post;

import com.anpopo.social.module.account.domain.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 400, nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private int numberOfReply = 0;
    private int numberOfReport = 0;

    public Comment(String comment, Account account, Post post) {
        this.comment = comment;
        this.account = account;
        this.post = post;
        post.getComments().add(this);

        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void setCommentFamily(Comment child) {
        this.numberOfReply++;
        this.children.add(child);
        child.setParent(this);
    }
}
