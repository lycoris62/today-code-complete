package sssdev.tcc.domain.user.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.request.UserFollowResponse;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

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
            var user = User.builder().password("test").username("username")
                .profileUrl("/api/test.png").description("description").nickname("핑크 공주").build();
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

        @DisplayName("없는 유저의 프로필 조회시 실패")
        @Test
        void fail() {
            // given
            var userId = 1L;
            // when
            ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.getProfile(userId));
            // then
            then(ex.getCode()).isEqualTo(ErrorCode.NOT_EXIST_USER);
        }
    }

    @DisplayName("팔로우")
    @Nested
    class FollowFunction {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            var fromUserId = 1L;
            var toUserId = 10L;
            var followerCount = 1L;
            var followingCount = 10L;
            var request = new UserFollowRequest(fromUserId, toUserId);

            var from = User.builder()
                .nickname("test")
                .description("description")
                .profileUrl("/api/test.png")
                .password("sample")
                .build();
            setField(from, "id", fromUserId);

            var to = User.builder()
                .nickname("test")
                .description("description")
                .profileUrl("/api/test.png")
                .password("sample")
                .build();
            setField(to, "id", toUserId);

            given(userRepository.findById(fromUserId)).willReturn(Optional.of(from));
            given(userRepository.findById(toUserId)).willReturn(Optional.of(to));
            given(followRepository.countFollowerByToId(toUserId)).willReturn(followerCount);
            given(followRepository.countFollowingByFromId(toUserId)).willReturn(followingCount);
            // when
            UserFollowResponse result = userService.follow(request);
            // then
            then(result.followerCount()).isEqualTo(followerCount);
            then(result.followingCount()).isEqualTo(followingCount);
            then(result.toUserId()).isEqualTo(toUserId);
        }

        @DisplayName("팔로우하는 유저가 없는 경우")
        @Test
        void fail_not_exist_follower() {
            // given
            var fromUserId = 1L;
            var toUserId = 10L;
            var request = new UserFollowRequest(fromUserId, toUserId);

            var to = User.builder()
                .nickname("test")
                .description("description")
                .profileUrl("/api/test.png")
                .password("sample")
                .build();
            setField(to, "id", toUserId);

            // when
            ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.follow(request));
            ErrorCode code = ex.getCode();
            // then
            then(code).isEqualTo(ErrorCode.NOT_EXIST_USER);
        }

        @DisplayName("팔로우당하는 유저가 없는 경우")
        @Test
        void fail_not_exist_following() {
            // given
            var fromUserId = 1L;
            var toUserId = 10L;
            var request = new UserFollowRequest(fromUserId, toUserId);

            var from = User.builder()
                .nickname("test")
                .description("description")
                .profileUrl("/api/test.png")
                .password("sample")
                .build();
            setField(from, "id", fromUserId);

            given(userRepository.findById(fromUserId)).willReturn(Optional.of(from));
            // when
            ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.follow(request));
            ErrorCode code = ex.getCode();
            // then
            then(code).isEqualTo(ErrorCode.NOT_EXIST_USER);
        }
    }

}