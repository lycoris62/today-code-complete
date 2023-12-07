package sssdev.tcc.domain.user.repository;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sssdev.tcc.domain.user.domain.Follow;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.support.RepositoryTest;

@DisplayName("팔로우 Repository 테스트")
class FollowRepositoryTest extends RepositoryTest {

    @Autowired
    FollowRepository followRepository;
    @Autowired
    UserRepository userRepository;

    @DisplayName("특정 팔로워 수 조회 동작 확인")
    @Test
    void followerCount() {
        // give
        var userA = userRepository.save(User.builder()
            .username("from")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        var userB = userRepository.save(User.builder()
            .username("to")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        followRepository.save(Follow.builder()
            .from(userA)
            .to(userB)
            .build());
        // when
        long followerCount = userB.getFollowerCount(followRepository);
        // then
        then(followerCount).isEqualTo(1);
    }

    @DisplayName("특정 팔로워 수 조회 동작 확인")
    @Test
    void followingCount() {
        // give
        var userA = userRepository.save(User.builder()
            .username("from")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        var userB = userRepository.save(User.builder()
            .username("to")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        userA.follow(userB);
        // when
        long followingCount = userA.getFollowingCount(followRepository);
        // then
        then(followingCount).isEqualTo(1);
    }
}