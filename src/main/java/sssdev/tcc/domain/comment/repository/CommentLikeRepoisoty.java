package sssdev.tcc.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.comment.domain.CommentLike;

public interface CommentLikeRepoisoty extends JpaRepository<CommentLike, Long> {

}
