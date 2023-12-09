package sssdev.tcc.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_POST;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;

import java.util.List;
import java.util.Optional;
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
import org.springframework.http.HttpStatus;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.dto.request.PostCreateRequest;
import sssdev.tcc.domain.post.dto.response.PostDetailResponse;
import sssdev.tcc.domain.post.repository.PostLikeRepository;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    FollowRepository followRepository;

    @InjectMocks
    PostService postService;

    @Nested
    @DisplayName("게시글 단건 조회")
    class PostGet {

        @DisplayName("성공 케이스 - 단건 게시글")
        @Test
        void get_post_success() {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            Post post = Post.builder().content("content01").user(user).build();
            setField(post, "id", 1L);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

            // when
            PostDetailResponse responseDto = postService.getPost(post.getId());

            // then
            assertThat(responseDto.username()).isEqualTo(user.getUsername());
            assertThat(responseDto.content()).isEqualTo(post.getContent());
            assertThat(responseDto.commentCount()).isEqualTo(0);
            assertThat(responseDto.likeCount()).isEqualTo(0);
        }

        @DisplayName("실패 케이스 - 없는 게시글")
        @Test
        void get_post_fail_n() {
            // given
            User user = User.builder().username("username").build();
            setField(user, "id", 1L);
            Post post = Post.builder().content("content01").user(user).build();
            setField(post, "id", 1L);

            given(postRepository.findById(post.getId()))
                .willThrow(new ServiceException(NOT_EXIST_POST));

            // when && then
            assertThatThrownBy(() -> postService.getPost(post.getId()))
                .isInstanceOf(ServiceException.class)
                .satisfies(exception -> {
                    ErrorCode errorCode = ((ServiceException) exception).getCode();
                    assertThat(errorCode.getMessage()).isEqualTo("게시글이 없습니다.");
                    assertThat(errorCode.getCode()).isEqualTo("2000");
                    assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                });
        }
    }

    @Nested
    @DisplayName("게시글 목록 조회")
    class PostsGet {

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

        @DisplayName("성공 케이스 - 팔로잉 게시글")
        @Test
        void get_following_posts_success() {
            // given
            User user1 = User.builder().username("username").build();
            setField(user1, "id", 1L);
            User user2 = User.builder().username("username").build();
            setField(user2, "id", 2L);

            user1.follow(user2);
            LoginUser loginUser = new LoginUser(user1.getId(), UserRole.USER);

            Post post1 = Post.builder().content("content01").user(user1).build();
            setField(post1, "id", 1L);
            Post post2 = Post.builder().content("content02").user(user2).build();
            setField(post2, "id", 2L);
            Post post3 = Post.builder().content("content03").user(user2).build();
            setField(post3, "id", 3L);

            List<Long> followingUserIdList = followRepository
                .findAllFollowIdByFromId(user1.getId(), PageRequest.of(0, 10));

            List<Post> postList = Stream.of(post1, post2, post3)
                .filter(post -> user1.getFollowingList()
                    .stream()
                    .map(follow -> follow.getTo().getId())
                    .anyMatch(id -> post.getUser().getId().equals(id)))
                .toList();

            Pageable pageable = PageRequest.of(0, 10);

            Page<Post> pageResult = new PageImpl<>(
                postList,
                pageable,
                2);

            given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
            given(postRepository.findAllByUserIdIn(followingUserIdList, pageable))
                .willReturn(pageResult);

            // when
            Page<PostDetailResponse> posts = postService.getFollowingPosts(loginUser, pageable);

            // then
            assertThat(posts.getContent().size()).isEqualTo(2);
        }

        @DisplayName("실패 케이스 - 사용자가 없음")
        @Test
        void get_following_posts_fail_not_exist_user() {
            // given
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            Pageable pageable = PageRequest.of(0, 10);

            given(userRepository.findById(anyLong()))
                .willThrow(new ServiceException(NOT_EXIST_USER));

            // when & then
            assertThatThrownBy(() -> postService.getFollowingPosts(loginUser, pageable))
                .isInstanceOf(ServiceException.class)
                .satisfies(exception -> {
                    ErrorCode errorCode = ((ServiceException) exception).getCode();
                    assertThat(errorCode.getMessage()).isEqualTo("사용자가 없습니다.");
                    assertThat(errorCode.getCode()).isEqualTo("1000");
                    assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
        }

        @Nested
        @DisplayName("게시글 생성")
        class CreatePost {

            @DisplayName("성공 케이스 - 게시글 성공")
            @Test
            void create_post_success() {
                // given
                User user = User.builder().username("username01").build();
                setField(user, "id", 1L);
                LoginUser loginUser = new LoginUser(user.getId(), UserRole.USER);

                PostCreateRequest requestDto = new PostCreateRequest("content01");

                given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

                // when
                postService.createPost(loginUser, requestDto);

                // then
                then(postRepository).should().save(any(Post.class));
            }

            @DisplayName("실패 케이스 - 사용자가 없음")
            @Test
            void create_post_fail_not_exist_user() {
                // given
                LoginUser loginUser = new LoginUser(1L, UserRole.USER);
                PostCreateRequest requestDto = new PostCreateRequest("content01");

                given(userRepository.findById(anyLong()))
                    .willThrow(new ServiceException(NOT_EXIST_USER));

                // when & then
                assertThatThrownBy(() -> postService.createPost(loginUser, requestDto))
                    .isInstanceOf(ServiceException.class)
                    .satisfies(exception -> {
                        ErrorCode errorCode = ((ServiceException) exception).getCode();
                        assertThat(errorCode.getMessage()).isEqualTo("사용자가 없습니다.");
                        assertThat(errorCode.getCode()).isEqualTo("1000");
                        assertThat(errorCode.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });
            }
        }
    }
}