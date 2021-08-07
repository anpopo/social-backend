package com.anpopo.social.account.repository;

import com.anpopo.social.account.domain.Account;
import com.anpopo.social.account.domain.QAccount;
import com.anpopo.social.interest.Interest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

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

    @Override
    public List<Account> findAccountByInterest(Interest interest) {
        JPQLQuery<Account> query = from(QAccount.account)
                .where(QAccount.account.deleted.isFalse()
                        .and(QAccount.account.interests.any().in(interest)));

        return query.fetch();

    }
}
