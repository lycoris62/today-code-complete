package sssdev.tcc.domain.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminPostUpdateResponse(
    Long id,
    String content
) {

}
