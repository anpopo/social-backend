package com.anpopo.social.module.post;

import com.anpopo.social.module.account.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"tags", "account"})
    Post findPostWithTagsAndAccountById(Long id);

    @EntityGraph(attributePaths = {"tags"})
    Post findPostWithTagsById(Long id);

    @EntityGraph(attributePaths = {"tags", "interest"})
    Post findPostWithTagsWithInterestById(Long id);

    List<Post> findAllByAccountOrderByModifiedAt(Account findAccount);

    @EntityGraph(attributePaths = {"account", "tags", "interest"})
    List<Post> findFirst20PostWithTagsWithInterestWithAccountByAccountInOrderByPostedAtDesc(List<Account> followingAccountList);
}
