package sssdev.tcc.domain.user.dto.response;

import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.repository.FollowRepository;

public record ProfileResponse(
    String nickname,
    long followerCount,
    long followingCount,
    String profileImageUrl,
    String description
) {

    public static ProfileResponse of(User user, FollowRepository followRepository) {
        return new ProfileResponse(
            user.getNickname(),
            user.getFollowerCount(followRepository),
            user.getFollowingCount(followRepository),
            user.getProfileUrl(),
            user.getDescription()
        );
    }
}
