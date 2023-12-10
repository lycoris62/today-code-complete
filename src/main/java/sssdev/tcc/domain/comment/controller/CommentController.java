package sssdev.tcc.domain.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.request.CommentModifyRequest;
import sssdev.tcc.domain.comment.dto.request.CommentRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.common.dto.response.RootResponse;
import sssdev.tcc.global.util.StatusUtil;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final StatusUtil statusUtil;

    @GetMapping("/comments")
    public ResponseEntity<?> getComments(HttpServletRequest servletRequest,
        @RequestBody CommentRequest request) {
        LoginUser loginUser = null;
        if (statusUtil.isLogin(servletRequest)) {
            loginUser = statusUtil.getLoginUser(servletRequest);
        }
        List<CommentResponse> responseList = commentService.getComments(request.postId(), loginUser);
        return ResponseEntity.ok(
            RootResponse.builder().code("200")
                .message("해당 게시물의 모든 댓글을 가져왔습니다.")
                .data(responseList).build());
    }

    @PostMapping("/comments")
    public ResponseEntity<?> createComments(
        @RequestBody CommentCreateRequest request,
        HttpServletRequest servletRequest) {

        LoginUser loginUser = statusUtil.getLoginUser(servletRequest);
        CommentResponse commentResponse = commentService.createComments(loginUser, request);

        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("댓글 생성 성공")
            .data(commentResponse)
            .build());
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<?> modifyComments(@PathVariable(name = "id") Long id,
        @RequestBody CommentModifyRequest request,
        HttpServletRequest servletRequest) {

        LoginUser loginUser = statusUtil.getLoginUser(servletRequest);
        CommentResponse response = commentService.modifyComments(id, request, loginUser);

        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("댓글 수정 성공")
            .data(response)
            .build());

    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComments(@PathVariable(name = "id") Long id,
        HttpServletRequest servletRequest) {
        LoginUser loginUser = statusUtil.getLoginUser(servletRequest);
        commentService.deleteComments(id, loginUser);

        return ResponseEntity.ok(RootResponse.builder()
            .code("200")
            .message("댓글 삭제 성공")
            .build());
    }
}