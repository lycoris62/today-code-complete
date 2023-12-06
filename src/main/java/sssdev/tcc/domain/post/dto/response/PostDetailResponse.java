package sssdev.tcc.domain.post.dto.response;

import sssdev.tcc.domain.post.domain.Post;

public record PostDetailResponse(
    Long postId,
    String username,
    String nickname,
    String content,
    Integer commentCount,
    Integer likeCount,
    Boolean isLike
) {

    public static PostDetailResponse of(Post post) {
        return new PostDetailResponse(
            post.getId(),
            post.getUser().getUsername(),
            post.getUser().getNickname(),
            post.getContent(),
            post.getCommentList().size(),
            post.getPostLikeList().size(),
            post.getPostLikeList().stream()
                .anyMatch(postLike -> postLike.getUser().getId().equals(post.getUser().getId()))
        );
    }
}
