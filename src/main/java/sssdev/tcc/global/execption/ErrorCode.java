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
    NOT_EXIST_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "1002", "깃허브 계정이 잘못되었습니다."),
    // post 2XXX
    NOT_EXIST_POST(HttpStatus.NOT_FOUND, "2000", "게시글이 없습니다."),

    // comment 3XXX
    NOT_EXIST_COMMENT(HttpStatus.NOT_FOUND, "3000", "댓글이 없습니다."),
    // admin 4XXX

    // global 5XXX
    NOT_LOGIN(HttpStatus.UNAUTHORIZED, "5000", "로그인이 필요합니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "5001", "권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
