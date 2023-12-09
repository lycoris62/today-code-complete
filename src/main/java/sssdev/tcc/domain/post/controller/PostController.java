package sssdev.tcc.domain.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.post.dto.request.PostCreateRequest;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.common.dto.response.RootResponse;
import sssdev.tcc.global.util.StatusUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final StatusUtil statusUtil; // 세션으로 유저를 가져오는 유틸

    /**
     * 게시글 목록 조회
     *
     * @param query 값이 있다면 query 포함 게시글 목록 반환
     */
    @GetMapping
    public ResponseEntity<RootResponse<Page<PostDetailResponse>>> getPosts(
        Pageable pageable,
        @RequestParam(name = "query", required = false, defaultValue = "") String query) {

        Page<PostDetailResponse> postList = postService.getPosts(pageable, query);

        return ResponseEntity.ok(RootResponse.<Page<PostDetailResponse>>builder()
            .data(postList)
            .build());
    }

    /**
     * 팔로우 중인 사람의 게시글 목록 조회
     */
    @GetMapping("/follow")
    public ResponseEntity<RootResponse<Page<PostDetailResponse>>> getFollowPosts(
        Pageable pageable,
        HttpServletRequest request) {

        LoginUser loginUser = statusUtil.getLoginUser(request);
        Page<PostDetailResponse> postList = postService.getFollowingPosts(loginUser, pageable);

        return ResponseEntity.ok(RootResponse.<Page<PostDetailResponse>>builder()
            .data(postList)
            .build());
    }

    /**
     * 게시글 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<RootResponse<PostDetailResponse>> getPost(
        @PathVariable(name = "id") Long id) {

        PostDetailResponse post = postService.getPost(id);

        return ResponseEntity.ok(RootResponse.<PostDetailResponse>builder()
            .data(post)
            .build());
    }

    /**
     * 게시글 생성
     */
    @PostMapping
    public ResponseEntity<?> createPost(
        PostCreateRequest requestDto,
        HttpServletRequest servletRequest) {

        LoginUser loginUser = statusUtil.getLoginUser(servletRequest);
        postService.createPost(loginUser, requestDto);

        return ResponseEntity.ok().build();
    }
}
