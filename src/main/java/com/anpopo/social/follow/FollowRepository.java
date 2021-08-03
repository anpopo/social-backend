package com.anpopo.social.follow;

import com.anpopo.social.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsFollowByFollowedAndFollow(Account findAccount, Account requestAccount);

    Follow findFollowByFollowedAndFollow(Account followAccount, Account requestAccount);
}