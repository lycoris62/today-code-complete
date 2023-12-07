package sssdev.tcc.domain.user.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.dto.request.ProfileUpdateRequest;
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

        @DisplayName("없는 유저의 프로필 조회시 실패")
        @Test
        void fail_when_get_profile() {
            // given
            var userId = 1L;
            // when
            ServiceException ex = assertThrows(ServiceException.class,
                () -> userService.getProfile(userId));
            // then
            then(ex.getCode()).isEqualTo(NOT_EXIST_USER);
        }

        @DisplayName("유저의 프로필 조회 실패")
        @Test
        void fail() {
            // given
            var userId2 = 2L;
            var userId = 1L;
            var user = User.builder()
                .password("test")
                .username("username")
                .profileUrl("/api/test.png")
                .description("description")
                .nickname("핑크 공주")
                .build();
            setField(user, "id", userId);

            given(userRepository.findById(userId2)).willThrow(new ServiceException(NOT_EXIST_USER));
            // when
            ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.getProfile(userId2));
            // then
            assertEquals("사용자가 없습니다.", exception.getCode().getMessage());
            assertEquals("1000", exception.getCode().getCode());
            assertEquals(HttpStatus.BAD_REQUEST, exception.getCode().getStatus());
        }
    }

    @DisplayName("유저 프로필 수정")
    @Nested
    class updateProfile {

        @DisplayName("닉네임 업데이트 성공)")
        @Test
        void success() {
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

            var request = new ProfileUpdateRequest("닉네임", null);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(followRepository.countFollowerByToId(userId)).willReturn(followerCount);
            given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);

            ProfileResponse updateProfile = userService.updateProfile(request, userId);

            then(updateProfile.nickname()).isEqualTo(request.nickname());
            then(updateProfile.followerCount()).isEqualTo(followerCount);
            then(updateProfile.followingCount()).isEqualTo(followingCount);
            then(updateProfile.profileImageUrl()).isEqualTo(user.getProfileUrl());
            then(updateProfile.description()).isEqualTo(user.getDescription());
        }

        @DisplayName("한줄평 업데이트 성공)")
        @Test
        void success_2() {
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

            var request = new ProfileUpdateRequest(null, "아 배고프다 밥먹고 싶다");

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(followRepository.countFollowerByToId(userId)).willReturn(followerCount);
            given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);

            ProfileResponse updateProfile = userService.updateProfile(request, userId);

            then(updateProfile.nickname()).isEqualTo(user.getNickname());
            then(updateProfile.followerCount()).isEqualTo(followerCount);
            then(updateProfile.followingCount()).isEqualTo(followingCount);
            then(updateProfile.profileImageUrl()).isEqualTo(user.getProfileUrl());
            then(updateProfile.description()).isEqualTo(request.description());
        }

        @DisplayName("닉네임 + 한줄평 업데이트 성공)")
        @Test
        void success_3() {
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

            var request = new ProfileUpdateRequest("닉네임", "아 배고프다 밥먹고 싶다");

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(followRepository.countFollowerByToId(userId)).willReturn(followerCount);
            given(followRepository.countFollowingByFromId(userId)).willReturn(followingCount);

            ProfileResponse updateProfile = userService.updateProfile(request, userId);

            then(updateProfile.nickname()).isEqualTo(request.nickname());
            then(updateProfile.followerCount()).isEqualTo(followerCount);
            then(updateProfile.followingCount()).isEqualTo(followingCount);
            then(updateProfile.profileImageUrl()).isEqualTo(user.getProfileUrl());
            then(updateProfile.description()).isEqualTo(request.description());
        }

        @DisplayName("업데이트 실패")
        @Test
        void fail() {
            // given
            var userId2 = 2L;
            var userId = 1L;
            var user = User.builder()
                .password("test")
                .username("username")
                .profileUrl("/api/test.png")
                .description("description")
                .nickname("핑크 공주")
                .build();
            setField(user, "id", userId);

            var request = new ProfileUpdateRequest(null, "아 배고프다 밥먹고 싶다");

            given(userRepository.findById(userId2)).willThrow(new ServiceException(NOT_EXIST_USER));
            // when

            // then
            ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateProfile(request, userId2));

            assertEquals("사용자가 없습니다.", exception.getCode().getMessage());
            assertEquals("1000", exception.getCode().getCode());
            assertEquals(HttpStatus.BAD_REQUEST, exception.getCode().getStatus());
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