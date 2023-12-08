package sssdev.tcc.domain.admin.dto;

public record ProfileListItem(
    Long id,
    String nickname,
    String profileImageUrl,
    String description
) {

}
