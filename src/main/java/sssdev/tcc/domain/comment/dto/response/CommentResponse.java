package sssdev.tcc.domain.comment.dto.response;

import lombok.Builder;
import sssdev.tcc.domain.comment.domain.Comment;

@Builder
public record CommentResponse(
    String writer,
    String content,
    Boolean likeStatus
) {
}