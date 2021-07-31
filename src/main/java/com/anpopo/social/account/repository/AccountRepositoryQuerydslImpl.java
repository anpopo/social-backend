package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.domain.QAccount;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class AccountRepositoryQuerydslImpl extends QuerydslRepositorySupport implements AccountRepositoryQuerydsl{
    public AccountRepositoryQuerydslImpl() {
        super(Account.class);
    }

    @Override
    public Account findAccountWithFollows(Account account) {

        return null;
    }
}
