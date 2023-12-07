package sssdev.tcc.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.service.UserService;
import sssdev.tcc.global.common.dto.response.RootResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<?> follow(@RequestBody UserFollowRequest request) {
        var body = userService.follow(request);
        return ResponseEntity.ok(
            RootResponse.builder()
                .code("200")
                .message("성공했습니다.")
                .data(body)
                .build()
        );
    }
}
