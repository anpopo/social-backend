package com.anpopo.social.account;

import com.anpopo.social.domain.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);

    @EntityGraph(value = "Account.withFollowings", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithFollowingsById(Long id);

    @EntityGraph(value = "Account.withFollowings", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithFollowersById(Long id);

    @EntityGraph(value = "Account.withFollowings", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithFollowingsByNickname(String nickname);

    @EntityGraph(value = "Account.withFollowings", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithFollowersByNickname(String nickname);
}
