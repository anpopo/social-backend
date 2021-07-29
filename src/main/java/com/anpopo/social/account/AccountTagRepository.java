package com.anpopo.social.account;

import com.anpopo.social.domain.Account;
import com.anpopo.social.domain.AccountTag;
import com.anpopo.social.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {
    AccountTag findByAccountAndTag(Account account, Tag tag);
}
