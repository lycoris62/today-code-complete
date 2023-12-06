package sssdev.tcc.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sssdev.tcc.domain.post.domain.Post;

public interface PostRepository extends PostReadRepository, JpaRepository<Post, Long> {

}
