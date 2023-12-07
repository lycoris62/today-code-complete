package sssdev.tcc.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.comment.domain.Comment;

public interface CommentRepository extends CommentReadRepository, JpaRepository<Comment, Long> {

    long countByPostId(Long postId);
}
