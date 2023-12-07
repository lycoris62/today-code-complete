package sssdev.tcc.domain.user.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
    private List<Follow> followingList = new ArrayList<>();

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

    public void follow(User to) {
        Follow follow = Follow.builder()
            .from(this)
            .to(to)
            .build();
        followingList.add(follow);
    }
}