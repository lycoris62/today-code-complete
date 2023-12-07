package sssdev.tcc.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @InjectMocks
    PostService postService;

    @Nested
    @DisplayName("게시글 목록 조회")
    class PostGet {

        @DisplayName("성공 케이스 - 전체 게시글")
        @Test
        void get_posts_success() {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user).build();
            setField(post2, "id", 2L);
            Pageable pageable = PageRequest.of(0, 10);

            Page<Post> pageResult = new PageImpl<>(List.of(post1, post2), pageable, 2);

            given(postRepository.findAll(pageable)).willReturn(pageResult);

            // when
            Page<PostDetailResponse> posts = postService.getPosts(pageable, "");

            // then
            assertThat(posts.getContent().size()).isEqualTo(2);
            assertThat(posts.getContent().get(0).content()).isEqualTo(post1.getContent());
            assertThat(posts.getContent().get(1).content()).isEqualTo(post2.getContent());
            assertThat(posts.getContent().get(0).username()).isEqualTo(user.getUsername());
        }

        @DisplayName("성공 케이스 - 검색 게시글")
        @Test
        void get_posts_by_query_success() {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user).build();
            setField(post2, "id", 2L);
            Pageable pageable = PageRequest.of(0, 10);
            String query = "01";

            Page<Post> pageResult = new PageImpl<>(
                Stream.of(post1, post2).filter(post -> post.getContent().contains(query)).toList(),
                pageable,
                2);

            given(postRepository.findAllByContentContaining(query, pageable))
                .willReturn(pageResult);

            // when
            Page<PostDetailResponse> posts = postService.getPosts(pageable, query);

            // then
            assertThat(posts.getContent().size()).isEqualTo(1);
            assertThat(posts.getContent().get(0).content()).isEqualTo(post1.getContent());
            assertThat(posts.getContent().get(0).username()).isEqualTo(user.getUsername());
        }
    }
}