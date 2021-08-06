package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.domain.QAccount;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class AccountRepositoryQuerydslImpl extends QuerydslRepositorySupport implements AccountRepositoryQuerydsl{
    public AccountRepositoryQuerydslImpl() {
        super(Account.class);
    }

    @Override
    public Account findAccountForProfilePage(String nickname) {
//        QAccount account = QAccount.account;
//        QFollow follow = QFollow.follow1;
//
//        JPQLQuery<Account> query = from(account)
//                .leftJoin(account.followers, follow).fetchJoin()
//                .leftJoin(follow.follow, account).fetchJoin()
//                .leftJoin(account.following, follow).fetchJoin()
//                .leftJoin(follow.followed, account).fetchJoin()
//                .where(account.nickname.eq(nickname)
//                        .and(account.deleted).isFalse()
//                        .and(follow.isAccepted.isTrue()))
//                .distinct();
//
//        return query.fetchOne();
        return null;
    }
}
