package sssdev.tcc.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.global.common.dto.response.RootResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

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
}
