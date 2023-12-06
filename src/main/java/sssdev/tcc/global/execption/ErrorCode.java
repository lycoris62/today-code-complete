package sssdev.tcc.global.execption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // user

    // post

    // comment

    // admin

    // global
    NOT_LOGIN(HttpStatus.UNAUTHORIZED, "5000", "로그인이 필요합니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
