package com.anpopo.social.module.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {

    Set<Tag> findByTitleIn(List<String> title);

    Optional<Tag> findByTitle(String title);
}
