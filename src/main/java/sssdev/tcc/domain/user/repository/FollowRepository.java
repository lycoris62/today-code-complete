package sssdev.tcc.domain.user.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sssdev.tcc.domain.user.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    long countFollowerByToId(Long from_id);

    long countFollowingByFromId(Long from_id);

    @Query("select u.id from Follow f join User u on f.to.id = u.id where f.from.id = :fromId")
    List<Long> findAllFollowIdByFromId(@Param("fromId") Long fromId, Pageable pageable);
}
