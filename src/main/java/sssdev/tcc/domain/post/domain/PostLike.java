package sssdev.tcc.domain.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sssdev.tcc.domain.model.BaseEntity;
import sssdev.tcc.domain.user.domain.User;

@Getter
@Entity
@Table(name = "POST_LIKE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
