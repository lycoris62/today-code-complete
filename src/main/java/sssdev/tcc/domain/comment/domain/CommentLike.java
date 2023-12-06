package sssdev.tcc.domain.comment.domain;

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
@Table(name = "COMMENT_LIKE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private CommentLike(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
