package sssdev.tcc.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.request.UserProfileUpdateRequest;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.dto.response.UserGithubInformation;
import sssdev.tcc.domain.user.service.UserService;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.common.dto.response.RootResponse;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;
import sssdev.tcc.global.util.StatusUtil;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StatusUtil statusUtil;

    @GetMapping("/login/github")
    public ResponseEntity<?> loginGithub(@RequestParam(name = "code") String code,
        HttpServletResponse res) {
        UserGithubInformation response = userService.loginGithub(code, res);
        return ResponseEntity.ok(
            RootResponse.builder()
                .code("200")
                .message("성공했습니다.")
                .data(response)
                .build()
        );
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable(name = "id") Long id) {
        ProfileResponse response = userService.getProfileList(id);
        return ResponseEntity.ok(
            RootResponse.builder()
                .code("200")
                .message("성공했습니다.")
                .data(response)
                .build()
        );
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody UserFollowRequest reqBody,
        HttpServletRequest request) {

        LoginUser loginUser = statusUtil.getLoginUser(request);
        if (!loginUser.id().equals(reqBody.fromUserId())) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }
        var body = userService.follow(reqBody);
        return ResponseEntity.ok(
            RootResponse.builder()
                .code("200")
                .message("성공했습니다.")
                .data(body)
                .build()
        );
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest body,
        HttpServletRequest request) {
        LoginUser loginUser = statusUtil.getLoginUser(request);
        ProfileResponse response = userService.updateProfile(body, loginUser.id());
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .data(response)
            .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        statusUtil.clearSession(request);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .build()
        );
    }
}
