package sssdev.tcc.global.common.dto;

import lombok.Builder;
import sssdev.tcc.domain.user.domain.UserRole;

@Builder
public record LoginUser(
    Long id,
    UserRole role
) {

}
