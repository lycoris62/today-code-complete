package sssdev.tcc.domain.admin.dto.response;

import lombok.Builder;
import sssdev.tcc.domain.user.domain.UserRole;

@Builder
public record AdminUserUpdateResponse(
    UserRole role,
    String description,
    String nickname,
    String profileUrl,
    Long userId
) {

}
