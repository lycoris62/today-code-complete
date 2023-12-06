package sssdev.tcc.domain.user.dto.response;

public record ProfileResponse(
    String nickname,
    Integer followerCount,
    Integer followingCount,
    String profileImageUrl,
    String description
) {
}
