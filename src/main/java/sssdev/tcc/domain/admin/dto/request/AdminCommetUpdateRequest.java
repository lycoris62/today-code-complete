package sssdev.tcc.domain.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminCommetUpdateRequest(
    Long userId,
    String content
) {

}
