package sssdev.tcc.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.user.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countFollowerByToId(Long from_id);

    long countFollowingByFromId(Long from_id);
}
