package com.anpopo.social.module.post.repository;

import com.anpopo.social.module.post.Comment;
import com.anpopo.social.module.post.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CommentRepositoryQuerydsl {

    List<Comment> findParentComments(Post post);
}
