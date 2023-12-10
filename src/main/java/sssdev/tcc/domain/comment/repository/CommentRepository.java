package sssdev.tcc.domain.comment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.comment.domain.Comment;

public interface CommentRepository extends CommentReadRepository, JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long id);

    long countByPostId(Long id);
}
