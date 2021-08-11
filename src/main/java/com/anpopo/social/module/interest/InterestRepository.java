package com.anpopo.social.module.interest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Interest findByInterest(String interest);
}
