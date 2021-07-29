package com.anpopo.social.post;

import com.anpopo.social.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<Post> findAllWithTagsByAccount(Account account);

    @EntityGraph(attributePaths = {"tags"})
    Post findPostWithTagsById(Long id);
}
