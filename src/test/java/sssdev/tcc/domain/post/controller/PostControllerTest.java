package sssdev.tcc.domain.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.common.dto.LoginUser;
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
    @DisplayName("게시글 목록 조회")
    class PostGet {

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
}