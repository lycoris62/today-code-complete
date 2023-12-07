package sssdev.tcc.domain.user.dto.request;

public record UserFollowRequest(
    Long fromUserId,
    Long toUserId
) {

}
