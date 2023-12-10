package sssdev.tcc.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubOAuth2Response {

    @JsonProperty("access_token")
    private String accessToken;
}
