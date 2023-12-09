package sssdev.tcc.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sssdev.tcc.domain.admin.dto.request.AdminPostUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminPostUpdateResponse;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.repository.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 모든 게시글을 가져옴. query 값이 존재하면 query 포함 게시글을 가져옴
     */
    public Page<PostDetailResponse> getPosts(Pageable pageable, String query) {

        if (query.isBlank()) {
            return postRepository.findAll(pageable)
                .map(post -> PostDetailResponse.of(post, commentRepository, postLikeRepository));
        }

        return postRepository.findAllByContentContaining(query, pageable)
            .map(post -> PostDetailResponse.of(post, commentRepository, postLikeRepository));
    }

    // todo
    public AdminPostUpdateResponse updatePostAdmin(Long id, AdminPostUpdateRequest request) {
        return null;
    }
}
