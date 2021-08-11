package com.anpopo.social.module.account.repository;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.interest.Interest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepositoryQuerydsl {
    Account findAccountForProfilePage(String nickname);

    List<Account> findAccountByInterest(Interest interest);
}
