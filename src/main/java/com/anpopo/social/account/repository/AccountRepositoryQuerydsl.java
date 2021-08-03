package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepositoryQuerydsl {
    Account findAccountForProfilePage(String nickname);
}
