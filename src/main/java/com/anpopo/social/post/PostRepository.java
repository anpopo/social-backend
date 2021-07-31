package com.anpopo.social.post;

import com.anpopo.social.account.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<Post> findAllWithTagsByAccount(Account account);

    @EntityGraph(attributePaths = {"tags"})
    Post findPostWithTagsById(Long id);
}
