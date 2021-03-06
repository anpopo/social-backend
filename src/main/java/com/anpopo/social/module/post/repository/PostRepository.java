package com.anpopo.social.module.post.repository;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"tags", "account"})
    Post findPostWithTagsAndAccountById(Long id);

    @EntityGraph(attributePaths = {"tags"})
    Post findPostWithTagsById(Long id);

    @EntityGraph(attributePaths = {"tags", "interest"})
    Post findPostWithTagsWithInterestById(Long id);

    List<Post> findAllByAccountOrderByModifiedAtDesc(Account findAccount);

    @EntityGraph(attributePaths = {"likeAccount"})
    Optional<Post> findPostWithLikeAccountById(Long postId);

    @EntityGraph(attributePaths = {"account", "tags", "interest", "likeAccount"})
    List<Post> findFirst20PostWithTagsWithInterestWithAccountWithLikeAccountByAccountInOrderByPostedAtDesc(List<Account> followingAccountList);

    boolean existsByAccountAndId(Account account, Long id);

    @EntityGraph(attributePaths = {"account", "tags", "interest", "likeAccount"})
    Post findPostWithTagsWithInterestWithAccountWithLikeAccountById(Long id);

}
