package sssdev.tcc.domain.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminCommentUpdateResponse(
    Long id,
    String content) {

}
