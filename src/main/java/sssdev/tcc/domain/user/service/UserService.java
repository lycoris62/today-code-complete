package sssdev.tcc.domain.user.service;

import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.request.UserFollowResponse;
import sssdev.tcc.domain.user.dto.request.UserProfileUpdateRequest;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.dto.response.UserGithubInformation;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    public UserGithubInformation loginGithub(String code, HttpServletResponse response) {
        String accessToken = getAccessToken(code, response);
        JsonNode userResourceNode = getUserName(accessToken);
        String login = userResourceNode.get("login").asText();
        String id = userResourceNode.get("id").asText();
        String avatar_url = userResourceNode.get("avatar_url").asText();
        return new UserGithubInformation(login, id, avatar_url);
    }

    private String getAccessToken(String code, HttpServletResponse response) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "Iv1.c66d220107393526");
        params.add("client_secret", "0c0a5028387f291b23cd5c74daf4334781373885");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
            "https://github.com/login/oauth/access_token", HttpMethod.POST,
            entity,
            JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    public JsonNode getUserName(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity,
            JsonNode.class).getBody();
    }

    public ProfileResponse getProfile(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        return ProfileResponse.of(user, followRepository);
    }

    @Transactional
    public ProfileResponse updateProfile(UserProfileUpdateRequest requst, Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        user.update(requst);
        return ProfileResponse.of(user, followRepository);
    }


    public UserFollowResponse follow(UserFollowRequest request) {
        User from = userRepository.findById(request.fromUserId())
            .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        User to = userRepository.findById(request.toUserId())
            .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));

        from.follow(to);
        return new UserFollowResponse(
            to.getId(),
            to.getFollowerCount(followRepository),
            to.getFollowingCount(followRepository)
        );
    }

}
