package sssdev.tcc.domain.user.domain;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sssdev.tcc.domain.user.repository.FollowRepository;

@DisplayName("유저 도메인 테스트")
class UserTest {

    @DisplayName("유저 팔로워 수 조회")
    @Test
    void get_follower_count() {
        // given
        var followRepository = mock(FollowRepository.class);
        var followerCount = 1L;

        var userId = 1L;
        var user = User.builder()
            .password("test")
            .username("username")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build();
        setField(user, "id", userId);

        given(followRepository.countFollowerByToId(userId)).willReturn(followerCount);
        // when
        long result = user.getFollowerCount(followRepository);
        // then
        then(result).isEqualTo(followerCount);
    }

    @DisplayName("유저 팔로잉 수 조회")
    @Test
    void get_following_count() {
        // given
        var followRepository = mock(FollowRepository.class);
        var followingCount = 10L;

        var userId = 1L;
        var user = User.builder()
            .password("test")
            .username("username")
            .profileUrl("/api/test.png")
            .description("description")
            .nickname("핑크 공주")
            .build();
        setField(user, "id", userId);

        given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);
        // when
        long result = user.getFollowingCount(followRepository);
        // then
        then(result).isEqualTo(followingCount);
    }
}