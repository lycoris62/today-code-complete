package sssdev.tcc.domain.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_POST;
import static sssdev.tcc.global.execption.ErrorCode.UNAUTHORIZED;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.dto.request.PostUpdateRequest;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ServiceException;
import sssdev.tcc.global.util.StatusUtil;
import sssdev.tcc.support.ControllerTest;


@DisplayName("Post API 테스트")
class PostControllerTest extends ControllerTest {

    @MockBean
    PostService postService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @MockBean
    StatusUtil statusUtil;

    @Nested
    @DisplayName("게시글 단건 조회")
    class PostGet {

        @DisplayName("성공 케이스 - 단건 게시글")
        @Test
        void get_post_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);
            PostDetailResponse responseDto = PostDetailResponse.of(post1, commentRepository,
                postLikeRepository);
            given(postService.getPost(user.getId())).willReturn(responseDto);

            // when && then
            mockMvc.perform(get("/api/posts/{id}", user.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content").value(post1.getContent()),
                    jsonPath("$.data.username").value(user.getUsername())
                );
        }

        @DisplayName("실패 케이스 - 없는 게시글")
        @Test
        void get_post_fail_not_exist_post() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            given(postService.getPost(user.getId()))
                .willThrow(new ServiceException(NOT_EXIST_POST));

            // when && then
            mockMvc.perform(get("/api/posts/{id}", user.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.message").value("게시글이 없습니다."),
                    jsonPath("$.code").value("2000")
                );
        }
    }

    @Nested
    @DisplayName("게시글 목록 조회")
    class PostsGet {

        String jsonPath = "$.data.content[?(@.content == '%s')]";

        @BeforeEach
        void setUp() {
            given(commentRepository.countByPostId(anyLong())).willReturn(0L);
            given(postLikeRepository.countByPostId(anyLong())).willReturn(0L);
            given(postLikeRepository.existsByUserIdAndPostId(anyLong(), anyLong()))
                .willReturn(false);
        }

        @DisplayName("성공 케이스 - 전체 게시글")
        @Test
        void get_posts_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user).build();
            setField(post2, "id", 2L);
            PostDetailResponse postDetail1 = PostDetailResponse.of(post1, commentRepository,
                postLikeRepository);
            PostDetailResponse postDetail2 = PostDetailResponse.of(post2, commentRepository,
                postLikeRepository);
            Pageable pageable = PageRequest.of(0, 10);

            Page<PostDetailResponse> pageResult = new PageImpl<>(
                List.of(postDetail1, postDetail2),
                pageable,
                2);

            given(postService.getPosts(any(Pageable.class), anyString())).willReturn(pageResult);

            // when // then
            mockMvc.perform(get("/api/posts")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "10"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(2),
                    jsonPath(jsonPath + ".username", "content01").value(postDetail1.username()),
                    jsonPath(jsonPath + ".nickname", "content01").value(postDetail1.nickname()),
                    jsonPath(jsonPath + ".commentCount", "content01").value(0),
                    jsonPath(jsonPath + ".likeCount", "content01").value(0),
                    jsonPath(jsonPath + ".content", "content01").value(postDetail1.content()),
                    jsonPath(jsonPath + ".content", "content02").value(postDetail2.content())
                );
        }

        @DisplayName("성공 케이스 - 특정 query 게시글")
        @Test
        void get_query_posts_success() throws Exception {
            // given
            String query = "01";
            User user = User.builder().username("username").build();
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user).build();
            setField(post2, "id", 2L);
            PostDetailResponse postDetail1 = PostDetailResponse.of(post1, commentRepository,
                postLikeRepository);
            PostDetailResponse postDetail2 = PostDetailResponse.of(post2, commentRepository,
                postLikeRepository);
            Pageable pageable = PageRequest.of(0, 10);

            List<PostDetailResponse> filteredPostList = Stream.of(postDetail1, postDetail2)
                .filter(post -> post.content().contains(query))
                .toList();

            Page<PostDetailResponse> pageResult = new PageImpl<>(
                filteredPostList, pageable, 2);

            given(postService.getPosts(any(Pageable.class), anyString())).willReturn(pageResult);

            // when // then
            mockMvc.perform(get("/api/posts")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "10")
                    .param("query", query))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(1),
                    jsonPath(jsonPath + ".username", "content01").value(postDetail1.username()),
                    jsonPath(jsonPath + ".nickname", "content01").value(postDetail1.nickname()),
                    jsonPath(jsonPath + ".commentCount", "content01").value(0),
                    jsonPath(jsonPath + ".likeCount", "content01").value(0),
                    jsonPath(jsonPath + ".content", "content01").value(postDetail1.content())
                );
        }

        @DisplayName("성공 케이스 - 팔로우 중인 유저의 게시글 목록 조회")
        @Test
        void get_following_posts_success() throws Exception {
            // given
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);

            User user1 = User.builder().username("username01").build();
            setField(user1, "id", 1L);
            User user2 = User.builder().username("username02").build();
            setField(user2, "id", 2L);

            user1.follow(user2);

            Post post1 = Post.builder().content("content01").user(user1).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user2).build();
            setField(post2, "id", 2L);
            Post post3 = Post.builder().content("content03").user(user2).build();
            setField(post3, "id", 3L);

            List<Long> followingUserIdList = user1.getFollowingList()
                .stream()
                .map(follow -> follow.getTo().getId())
                .toList();

            List<PostDetailResponse> filteredPostList = Stream.of(post1, post2, post3)
                .filter(post -> followingUserIdList.contains(post.getUser().getId()))
                .map(post -> PostDetailResponse.of(post, commentRepository, postLikeRepository))
                .toList();

            Pageable pageable = PageRequest.of(0, 10);

            Page<PostDetailResponse> pageResult = new PageImpl<>(
                filteredPostList, pageable, filteredPostList.size());

            given(statusUtil.getLoginUser(any(HttpServletRequest.class))).willReturn(loginUser);
            given(postService.getFollowingPosts(any(LoginUser.class), any(Pageable.class)))
                .willReturn(pageResult);

            // when // then
            mockMvc.perform(get("/api/posts/follow")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "10"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content.size()").value(2),
                    jsonPath("$.data.content[?(@.content == '%s')]", "content01").doesNotExist(),
                    jsonPath("$.data.content[?(@.content == '%s')]", "content02").exists(),
                    jsonPath("$.data.content[?(@.content == '%s')]", "content03").exists()
                );

        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class PostUpdate {

        @DisplayName("성공 케이스 - 게시글 수정")
        @Test
        void update_post_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            PostUpdateRequest requestDto = new PostUpdateRequest("content02");

            PostDetailResponse responseDto = PostDetailResponse
                .of(post1, commentRepository, postLikeRepository);

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            given(postService
                .updatePost(anyLong(), any(LoginUser.class), any(PostUpdateRequest.class)))
                .willReturn(responseDto);

            String json = objectMapper.writeValueAsString(requestDto);

            // when && then
            mockMvc.perform(patch("/api/posts/{id}", post1.getId())
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.content").value(requestDto.getContent()),
                    jsonPath("$.data.username").value(user.getUsername())
                );
        }

        @DisplayName("실패 케이스 - 다른 사용자")
        @Test
        void update_post_fail_not_unauthorized() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            PostUpdateRequest requestDto = new PostUpdateRequest("content02");

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            given(postService
                .updatePost(anyLong(), any(LoginUser.class), any(PostUpdateRequest.class)))
                .willThrow(new ServiceException(UNAUTHORIZED));

            String json = objectMapper.writeValueAsString(requestDto);

            // when && then
            mockMvc.perform(patch("/api/posts/{id}", post1.getId())
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isUnauthorized(),
                    jsonPath("$.message").value("권한이 없습니다."),
                    jsonPath("$.code").value("5001")
                );
        }

        @DisplayName("실패 케이스 - 없는 게시글")
        @Test
        void update_post_fail_not_exist_post() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            PostUpdateRequest requestDto = new PostUpdateRequest("content02");

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            given(postService
                .updatePost(anyLong(), any(LoginUser.class), any(PostUpdateRequest.class)))
                .willThrow(new ServiceException(NOT_EXIST_POST));

            String json = objectMapper.writeValueAsString(requestDto);

            // when && then
            mockMvc.perform(patch("/api/posts/{id}", post1.getId())
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.message").value("게시글이 없습니다."),
                    jsonPath("$.code").value("2000")
                );
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    class PostDelete {

        @DisplayName("성공 케이스 - 게시글 삭제")
        @Test
        void delete_post_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            // when && then
            mockMvc.perform(delete("/api/posts/{id}", post1.getId()))
                .andDo(print())
                .andExpectAll(
                    status().isOk()
                );
        }
    }

    @Nested
    @DisplayName("게시글 좋아요")
    class PostLike {

        @DisplayName("성공 케이스 - 게시글 좋아요 추가")
        @Test
        void like_post_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            // when && then
            mockMvc.perform(post("/api/posts/{id}/like", post1.getId()))
                .andDo(print())
                .andExpectAll(
                    status().isOk()
                );
        }

        @DisplayName("성공 케이스 - 게시글 좋아요 삭제")
        @Test
        void unlike_post_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content02").user(user).build();
            setField(post1, "id", 1L);

            given(statusUtil.getLoginUser(any(HttpServletRequest.class)))
                .willReturn(loginUser);

            // when && then
            mockMvc.perform(delete("/api/posts/{id}/like", post1.getId()))
                .andDo(print())
                .andExpectAll(
                    status().isOk()
                );
        }
    }
}