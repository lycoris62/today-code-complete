package sssdev.tcc.domain.admin.dto.request;

import lombok.Builder;
import sssdev.tcc.domain.user.domain.UserRole;

@Builder
public record AdminUserUpdateRequest(
    Long userId,
    UserRole role,
    String description,
    String nickname,
    String profileUrl
) {

}
