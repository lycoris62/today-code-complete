package sssdev.tcc.domain.post.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.post.domain.Post;

public interface PostRepository extends PostReadRepository, JpaRepository<Post, Long> {

    Page<Post> findAllByContentContaining(String query, Pageable pageable);

    Page<Post> findAllByUserIdIn(List<Long> followingUserIdList, Pageable pageable);
}
