package sssdev.tcc.domain.comment.dto.request;

public record CommentCreateRequest(
    String content,
    Long postId
) {

}
