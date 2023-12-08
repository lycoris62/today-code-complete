package sssdev.tcc.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.admin.dto.ProfileListItem;
import sssdev.tcc.domain.admin.dto.request.AdminPostUpdateRequest;
import sssdev.tcc.domain.admin.dto.request.AdminUserListGetRequest;
import sssdev.tcc.domain.admin.dto.request.AdminUserUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminPostUpdateResponse;
import sssdev.tcc.domain.admin.dto.response.AdminUserUpdateResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.domain.user.service.UserService;
import sssdev.tcc.global.common.dto.response.RootResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final CommentService commentService;
    private final PostService postService;

    @PatchMapping("/users")
    public ResponseEntity<?> updateProfile(@RequestBody AdminUserUpdateRequest body) {
        AdminUserUpdateResponse response = userService.updateProfileAdmin(body);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .data(response)
            .build());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserList(Pageable pageable,
        @RequestBody AdminUserListGetRequest request) {
        Page<ProfileListItem> body = userService.getProfileListAdmin(request, pageable);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .data(body)
            .build()
        );
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable(name = "id") Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .build());
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(name = "id") Long id) {
        commentService.deletePost(id);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .build());
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable(name = "id") Long id,
        @RequestBody AdminPostUpdateRequest request) {
        AdminPostUpdateResponse body = postService.updatePost(id, request);
        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("성공했습니다.")
            .data(body)
            .build());
    }
}