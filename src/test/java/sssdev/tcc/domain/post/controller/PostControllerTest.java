package sssdev.tcc.domain.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.support.ControllerTest;

@DisplayName("Post API 테스트")
class PostControllerTest extends ControllerTest {

    @MockBean
    PostService postService;

    @Nested
    @DisplayName("게시글 목록 조회")
    class PostGet {

        @DisplayName("성공 케이스 - 전체 게시글")
        @Test
        void get_posts_success() throws Exception {
            // given
            User user = User.builder().username("username").build();
            Post post1 = Post.builder().content("content01").user(user).build();
            Post post2 = Post.builder().content("content02").user(user).build();
            PostDetailResponse postDetail1 = PostDetailResponse.of(post1);
            PostDetailResponse postDetail2 = PostDetailResponse.of(post2);
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
                    jsonPath("$.data.content[0].username").value(postDetail1.username()),
                    jsonPath("$.data.content[0].nickname").value(postDetail1.nickname()),
                    jsonPath("$.data.content[0].commentCount").value(postDetail1.commentCount()),
                    jsonPath("$.data.content[0].likeCount").value(postDetail1.likeCount()),
                    jsonPath("$.data.content[0].content").value(postDetail1.content()),
                    jsonPath("$.data.content[1].content").value(postDetail2.content())
                );
        }

        @DisplayName("성공 케이스 - 특정 query 게시글")
        @Test
        void get_query_posts_success() throws Exception {
            // given
            String query = "01";
            User user = User.builder().username("username").build();
            Post post1 = Post.builder().content("content01").user(user).build();
            Post post2 = Post.builder().content("content02").user(user).build();
            PostDetailResponse postDetail1 = PostDetailResponse.of(post1);
            PostDetailResponse postDetail2 = PostDetailResponse.of(post2);
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
                    jsonPath("$.data.content[0].username").value(postDetail1.username()),
                    jsonPath("$.data.content[0].nickname").value(postDetail1.nickname()),
                    jsonPath("$.data.content[0].commentCount").value(postDetail1.commentCount()),
                    jsonPath("$.data.content[0].likeCount").value(postDetail1.likeCount()),
                    jsonPath("$.data.content[0].content").value(postDetail1.content())
                );
        }
    }

}