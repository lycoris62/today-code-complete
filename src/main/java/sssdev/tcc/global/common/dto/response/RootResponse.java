package sssdev.tcc.global.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@JsonInclude(Include.NON_NULL)
@Builder
public record RootResponse<T>(
    String code,
    String message,
    T data
) {

}