package sssdev.tcc.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.post.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    long countByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
