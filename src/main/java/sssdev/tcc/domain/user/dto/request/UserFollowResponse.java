package sssdev.tcc.domain.user.dto.request;

public record UserFollowResponse(
    Long toUserId,
    Long followerCount,
    Long followingCount
) {

}
