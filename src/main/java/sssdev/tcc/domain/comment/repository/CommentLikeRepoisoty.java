package sssdev.tcc.domain.comment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.comment.domain.CommentLike;

public interface CommentLikeRepoisoty extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findByUserId(Long id);
}
