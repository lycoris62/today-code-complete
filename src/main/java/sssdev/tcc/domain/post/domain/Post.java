package sssdev.tcc.domain.post.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.model.BaseEntity;
import sssdev.tcc.domain.post.dto.request.PostUpdateRequest;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.user.domain.User;

@Getter
@Entity
@Table(name = "POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostLike> postLikeList = new ArrayList<>();

    @Builder
    private Post(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public long getCommentCount(CommentRepository repository) {
        return repository.countByPostId(getId());
    }

    public long getLikeCount(PostLikeRepository repository) {
        return repository.countByPostId(getId());
    }

    public boolean getIsLike(PostLikeRepository repository) {
        return repository.existsByUserIdAndPostId(user.getId(), getId());
    }

    public void updateContent(PostUpdateRequest request) {
        this.content = request.getContent();
    }

    public void like(User user) {

        PostLike postLike = PostLike
            .builder()
            .post(this)
            .user(user)
            .build();

        postLikeList.add(postLike);
    }

    public void unlike(PostLike postLike) {
        postLikeList.remove(postLike);
    }
}