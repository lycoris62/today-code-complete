package sssdev.tcc.domain.user.domain;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;
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
        var user = User.builder().password("test").username("username").profileUrl("/api/test.png")
            .description("description").nickname("핑크 공주").build();
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
        var user = User.builder().password("test").username("username").profileUrl("/api/test.png")
            .description("description").nickname("핑크 공주").build();
        setField(user, "id", userId);

        given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);
        // when
        long result = user.getFollowingCount(followRepository);
        // then
        then(result).isEqualTo(followingCount);
    }

    @DisplayName("팔로우 이후에 팔로우 사이즈가 증가한다.")
    @Test
    void follow() {
        // given
        var from = User.builder().password("test").username("username").profileUrl("/api/test.png")
            .description("description").nickname("핑크 공주").build();
        setField(from, "id", 1L);

        var to = User.builder().password("test").username("username").profileUrl("/api/test.png")
            .description("description").nickname("핑크 공주").build();
        setField(to, "id", 2L);
        // when
        from.follow(to);
        // then
        List<Follow> follwingList = (List<Follow>) getField(from, "followingList");
        then(follwingList).extracting("from.id", "to.id")
            .containsExactly(tuple(from.getId(), to.getId()));
    }
}