package sssdev.tcc.domain.user.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    FollowRepository followRepository;
    UserService userService;

    @BeforeEach
    void init() {
        this.userService = new UserService(userRepository, followRepository);
    }

    @DisplayName("프로필 조회")
    @Nested
    class GetProfile {

        @DisplayName("유저의 프로필 조회 성공")
        @Test
        void success() {
            // given
            var userId = 1L;
            var followerCount = 1L;
            var followingCount = 10L;
            var user = User.builder()
                .password("test")
                .username("username")
                .profileUrl("/api/test.png")
                .description("description")
                .nickname("핑크 공주")
                .build();
            setField(user, "id", userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(followRepository.countFollowerByToId(userId)).willReturn(followerCount);
            given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);
            // when
            ProfileResponse profile = userService.getProfile(userId);
            // then
            then(profile.nickname()).isEqualTo(user.getNickname());
            then(profile.followerCount()).isEqualTo(followerCount);
            then(profile.followingCount()).isEqualTo(followingCount);
            then(profile.profileImageUrl()).isEqualTo(user.getProfileUrl());
            then(profile.description()).isEqualTo(user.getDescription());
        }
    }
}