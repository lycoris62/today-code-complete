package sssdev.tcc.domain.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminCommentUpdateRequest(
    Long userId,
    String content
) {

}
