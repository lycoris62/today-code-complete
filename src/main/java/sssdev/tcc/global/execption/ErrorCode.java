package sssdev.tcc.global.execption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // user 1XXX
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "1000", "사용자가 없습니다."),
    CHECK_USER(HttpStatus.BAD_REQUEST, "1001", "본인이 아닙니다."),

    // post 2XXX

    // comment 3XXX

    // admin 4XXX

    // global 5XXX
    NOT_LOGIN(HttpStatus.UNAUTHORIZED, "5000", "로그인이 필요합니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "5001", "권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
