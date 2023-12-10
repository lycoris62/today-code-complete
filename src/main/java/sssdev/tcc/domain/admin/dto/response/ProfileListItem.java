package sssdev.tcc.domain.admin.dto.response;

public record ProfileListItem(
    Long id,
    String nickname,
    String profileImageUrl,
    String description
) {

}
