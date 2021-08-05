package com.anpopo.social.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Set<Tag> findByTitleIn(List<String> title);

    Optional<Tag> findByTitle(String title);
}
