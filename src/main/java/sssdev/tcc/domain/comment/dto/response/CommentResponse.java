package sssdev.tcc.domain.comment.dto.response;

public record CommentResponse(
    String writer,
    String content,
    boolean likeStatus
) {

}