package com.anpopo.social.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface FollowerRepository extends JpaRepository<Follower, Long> {
}
