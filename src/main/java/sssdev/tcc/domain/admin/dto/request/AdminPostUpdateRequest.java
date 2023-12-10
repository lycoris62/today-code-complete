package sssdev.tcc.domain.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminPostUpdateRequest(
    Long userId,
    String content
) {

}
