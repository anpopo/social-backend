package com.anpopo.social.module.account.repository;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.account.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsFollowByFollowedAndFollow(Account findAccount, Account requestAccount);

    Follow findFollowByFollowedAndFollow(Account followAccount, Account requestAccount);

    List<Follow> findByFollowed(Account account);
}
