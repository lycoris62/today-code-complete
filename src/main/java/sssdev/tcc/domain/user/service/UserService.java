package sssdev.tcc.domain.user.service;

import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_OAUTH_TOKEN;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import sssdev.tcc.domain.admin.dto.request.AdminUserUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminUserUpdateResponse;
import sssdev.tcc.domain.admin.dto.response.ProfileListItem;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.request.UserFollowResponse;
import sssdev.tcc.domain.user.dto.request.UserProfileUpdateRequest;
import sssdev.tcc.domain.user.dto.request.UserProfileUrlUpdateRequest;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.dto.response.UserGithubInformation;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;
import sssdev.tcc.global.util.StatusUtil;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final StatusUtil statusUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserGithubInformation loginGithub(String code, HttpServletRequest request,
        HttpServletResponse response) {
        String accessToken = getAccessToken(code, response);
        if (accessToken == null) {
            throw new ServiceException(NOT_EXIST_OAUTH_TOKEN);
        }
        JsonNode userResourceNode = getUserName(accessToken);
        String login = userResourceNode.get("login").asText();
        String id = userResourceNode.get("id").asText();
        String avatar_url = userResourceNode.get("avatar_url").asText();

        Optional<User> user = userRepository.findByUsername(id);
        if (user.isEmpty()) {
            userRepository.save(User.builder()
                .username(id)
                .nickname(login)
                .profileUrl(avatar_url)
                .description("안녕하세요")
                .build());
            user = userRepository.findByUsername(id);
        }

        LoginUser loginUser = LoginUser.builder().id(user.get().getId()).role(user.get().getRole())
            .build();
        statusUtil.login(loginUser, request);

        return new UserGithubInformation(login, id, avatar_url);
    }

    private String getAccessToken(String code, HttpServletResponse response) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "Iv1.c66d220107393526");
        params.add("client_secret", "0c0a5028387f291b23cd5c74daf4334781373885");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
            "https://github.com/login/oauth/access_token", HttpMethod.POST,
            entity,
            JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode != null ? accessTokenNode.get("access_token").asText() : null;
    }

    public JsonNode getUserName(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity,
            JsonNode.class).getBody();
    }

    public ProfileResponse getProfileList(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        return ProfileResponse.of(user, followRepository);
    }

    @Transactional
    public ProfileResponse updateProfile(UserProfileUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        user.update(request);
        return ProfileResponse.of(user, followRepository);
    }

    @Transactional
    public ProfileResponse updateProfileUrl(UserProfileUrlUpdateRequest body, Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        user.updateUrl(body.profileUrl());
        return ProfileResponse.of(user, followRepository);
    }

    @Transactional
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

    @Transactional
    public AdminUserUpdateResponse updateProfileAdmin(AdminUserUpdateRequest body) {
        User user = userRepository.findById(body.userId())
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));
        user.update(new UserProfileUpdateRequest(body.nickname(), body.description()));
        user.updateRol(body.role());
        user.updateUrl(body.profileUrl());
        return new AdminUserUpdateResponse(user.getRole(), user.getDescription(),
            user.getProfileUrl(), user.getNickname(), user.getId());
    }

    public List<ProfileListItem> getProfileListAdmin(
        Pageable pageable) {
        Page<User> list = userRepository.findAll(pageable);
        if (list.isEmpty()) {
            throw new ServiceException(NOT_EXIST_USER);
        }

        return list.stream()
            .map(u -> new ProfileListItem(
                u.getId(),
                u.getNickname(),
                u.getProfileUrl(),
                u.getDescription())).toList();
    }

}
