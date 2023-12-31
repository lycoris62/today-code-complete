package sssdev.tcc.domain.post.service;

import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_POST;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;
import static sssdev.tcc.global.execption.ErrorCode.UNAUTHORIZED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sssdev.tcc.domain.admin.dto.request.AdminPostUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminPostUpdateResponse;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.dto.request.PostCreateRequest;
import sssdev.tcc.domain.post.dto.request.PostUpdateRequest;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ServiceException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final FollowRepository followRepository;

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

    /**
     * 팔로우 중인 사람들의 게시글 목록을 가져옴.
     */
    public Page<PostDetailResponse> getFollowingPosts(LoginUser loginUser, Pageable pageable) {

        User user = userRepository.findById(loginUser.id())
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));

        List<Long> followingUserIdList = followRepository.findAllFollowIdByFromId(user.getId(),
            PageRequest.of(0, 10));

        return postRepository.findAllByUserIdIn(followingUserIdList, pageable)
            .map(post -> PostDetailResponse.of(post, commentRepository, postLikeRepository));
    }

    /**
     * 게시글 단건 조회
     */
    public PostDetailResponse getPost(Long id) {

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));

        return PostDetailResponse.of(post, commentRepository, postLikeRepository);
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public void createPost(LoginUser loginUser, PostCreateRequest requestDto) {

        User user = userRepository.findById(loginUser.id())
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));

        Post post = Post.builder()
            .user(user)
            .content(requestDto.getContent())
            .build();

        postRepository.save(post);
    }

    /**
     * 게시글 수정 - 관리자가 아닌 사용자용
     *
     * @return 변경된 게시글 DTO 반환
     */
    @Transactional
    public PostDetailResponse updatePost(Long id, LoginUser loginUser, PostUpdateRequest request) {

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));

        checkSameUser(loginUser, post);

        post.updateContent(request);

        return PostDetailResponse.of(post, commentRepository, postLikeRepository);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void delete(Long id, LoginUser loginUser) {

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));

        checkSameUser(loginUser, post);

        postRepository.delete(post);
    }

    private void checkSameUser(LoginUser loginUser, Post post) {
        if (!post.getUser().getId().equals(loginUser.id())) {
            throw new ServiceException(UNAUTHORIZED);
        }
    }

    /**
     * 게시글 좋아요 추가
     */
    @Transactional
    public void likePost(Long id, LoginUser loginUser) {

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));

        User user = userRepository.findById(loginUser.id())
            .orElseThrow(() -> new ServiceException(NOT_EXIST_USER));

        post.like(user);
    }

    /**
     * 게시글 좋아요 삭제
     */
    @Transactional
    public void unlikePost(Long id, LoginUser loginUser) {

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));

        postLikeRepository
            .findByUserIdAndPostId(loginUser.id(), id)
            .ifPresent(post::unlike);
    }

    // todo
    @Transactional
    public AdminPostUpdateResponse updatePostAdmin(Long id, AdminPostUpdateRequest request) {
        PostUpdateRequest postRequest = new PostUpdateRequest(request.content());
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));
        post.updateContent(postRequest);
        return AdminPostUpdateResponse.builder().id(id).content(request.content()).build();
    }

    // todo
    @Transactional
    public void deletePostAdmin(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));
        postRepository.delete(post);
    }
}
