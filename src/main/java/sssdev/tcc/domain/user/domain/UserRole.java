package sssdev.tcc.domain.user.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    USER("ROLE_USER", "유저권한"),
    ADMIN("ROLE_ADMIN", "어드민권한");

    private final String authority;
    @Getter
    private final String description;

    private UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
