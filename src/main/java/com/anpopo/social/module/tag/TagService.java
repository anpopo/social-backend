package com.anpopo.social.module.tag;

import com.anpopo.social.module.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public void findOrCreateNew(String tagTitle, Account account) {
       tagRepository.findByTitle(tagTitle)
                .orElseGet(() -> tagRepository.save(
                        Tag.builder()
                                .title(tagTitle)
                                .account(account)
                                .createdAt(LocalDateTime.now())
                                .build()
                        )
                );
    }

}
