package com.anpopo.social.module.post.repository;

import com.anpopo.social.module.account.domain.QAccount;
import com.anpopo.social.module.post.Comment;
import com.anpopo.social.module.post.Post;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.anpopo.social.module.post.QComment.comment1;

public class CommentRepositoryQuerydslImpl extends QuerydslRepositorySupport implements CommentRepositoryQuerydsl{
    public CommentRepositoryQuerydslImpl() {
        super(Comment.class);
    }

    @Override
    public List<Comment> findParentComments(Post post) {
        JPQLQuery<Comment> query = from(comment1)
                .leftJoin(comment1.account, QAccount.account)
                .where(comment1.parent.isNull().and(comment1.post.eq(post)))
                .orderBy(comment1.modifiedAt.desc());

        return query.fetch();
    }


}
