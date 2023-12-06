package sssdev.tcc.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sssdev.tcc.domain.model.BaseEntity;
import sssdev.tcc.domain.user.repository.FollowRepository;

@Getter
@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String profileUrl;

    @Builder
    private User(String username, String password, String nickname, String description,
        String profileUrl) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.description = description;
        this.profileUrl = profileUrl;
    }

    public long getFollowingCount(FollowRepository repository) {
        return repository.countFollowingByFromId(getId());
    }

    public long getFollowerCount(FollowRepository repository) {
        return repository.countFollowerByToId(getId());
    }
}