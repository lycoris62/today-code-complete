package sssdev.tcc.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
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
            .password("test")
            .username("from")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        var userB = userRepository.save(User.builder()
            .password("test")
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
            .password("test")
            .username("from")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build());

        var userB = userRepository.save(User.builder()
            .password("test")
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

    @DisplayName("특정 id의 유저가 팔로잉하는 유저들의 id 목록 반환 확인")
    @Test
    void test1() {
        User userA = userRepository.save(User.builder()
            .password("test")
            .username("from")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주1")
            .build());

        User userB = userRepository.save(User.builder()
            .password("test")
            .username("to")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주2")
            .build());

        userA.follow(userB);

        List<Long> followingIdList = followRepository.findAllFollowIdByFromId(userA.getId());

        assertThat(followingIdList).size().isEqualTo(1);
        assertThat(followingIdList.get(0)).isEqualTo(userB.getId());
    }
}