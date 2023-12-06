package sssdev.tcc.global.execption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sssdev.tcc.global.common.dto.response.RootResponse;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleException(ServiceException ex) {
        ErrorCode code = ex.getCode();
        RootResponse<Object> response = RootResponse.builder()
            .code(code.getCode())
            .message(code.getMessage())
            .build();
        return ResponseEntity.status(code.getStatus())
            .body(response);
    }
}
