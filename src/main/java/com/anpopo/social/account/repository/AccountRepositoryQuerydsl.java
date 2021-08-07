package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.interest.Interest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepositoryQuerydsl {
    Account findAccountForProfilePage(String nickname);

    List<Account> findAccountByInterest(Interest interest);
}
