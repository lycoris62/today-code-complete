package sssdev.tcc.domain.user.dto.response;

public record UserGithubInformation(
    String login,
    String id,
    String avatar_url
) {

}
