package com.anpopo.social.module.post.repository;

import com.anpopo.social.module.post.Comment;
import com.anpopo.social.module.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuerydsl {

    Set<Comment> findCommentWithAccountByPostOrderByModifiedAtDesc(Post post);

}
