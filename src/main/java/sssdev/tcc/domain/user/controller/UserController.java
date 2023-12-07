package sssdev.tcc.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.user.dto.request.ProfileUpdateRequest;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
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

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable(name = "id") Long id) {
        ProfileResponse response = userService.getProfile(id);
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
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest body,
        HttpServletRequest request) {
        LoginUser loginUser = statusUtil.getLoginUser(request);
        ProfileResponse response = userService.updateProfile(body, loginUser.id());
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .data(response)
            .build());
    }

}
