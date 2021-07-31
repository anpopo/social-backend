package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;

public interface AccountRepositoryQuerydsl {
    Account findAccountWithFollows(Account account);
}
