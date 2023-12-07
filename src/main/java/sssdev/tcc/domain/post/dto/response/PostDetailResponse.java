package sssdev.tcc.domain.post.dto.response;

import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.repository.PostLikeRepository;

public record PostDetailResponse(
    long postId,
    String username,
    String nickname,
    String content,
    long commentCount,
    long likeCount,
    boolean isLike
) {

    public static PostDetailResponse of(
        Post post,
        CommentRepository commentRepository,
        PostLikeRepository postLikeRepository) {

        return new PostDetailResponse(
            post.getId(),
            post.getUser().getUsername(),
            post.getUser().getNickname(),
            post.getContent(),
            post.getCommentCount(commentRepository),
            post.getLikeCount(postLikeRepository),
            post.getIsLike(postLikeRepository)
        );
    }
}
