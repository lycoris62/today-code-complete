package sssdev.tcc.domain.user.dto.request;

public record UserPasswordUpdateRequest(
    String password,
    String changePassword,
    String checkedChange
) {

}
