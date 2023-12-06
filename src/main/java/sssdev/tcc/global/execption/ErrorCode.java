package sssdev.tcc.global.execption;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // user

    // post

    // comment

    // admin

    // global
    ;
    private HttpStatus status;
    private String code;
    private String message;
}
