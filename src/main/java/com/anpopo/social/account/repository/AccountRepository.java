package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryQuerydsl {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);

    @EntityGraph("Account.withInterest")
    Optional<Account> findAccountWithInterestById(Long id);

    @EntityGraph("Account.withFollowers")
    Account findAccountWithFollowersById(Long id);

    @EntityGraph("Account.withFollowing")
    Account findAccountWithFollowingById(Long id);

    @EntityGraph("Account.withFollowers")
    Account findAccountWithFollowersByNickname(String nickname);

    @EntityGraph("Account.withFollowing")
    Account findAccountWithFollowingByNickname(String nickname);

    @EntityGraph("Account.withFollowersAndFollowingAndAccount")
    Account findAccountWithFollowersAndFollowingByNickname(String nickname);

    @EntityGraph("Account.withFollowersAndFollowersAccount")
    Account findAccountWithFollowersWithAccountById(Long id);

    @EntityGraph("Account.withFollowingAndFollowingAccount")
    Account findAccountWithFollowingWithAccountById(Long id);
}
