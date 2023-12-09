package sssdev.tcc.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static sssdev.tcc.global.execption.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.comment.domain.CommentLike;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentLikeRepoisoty commentLikeRepoisoty;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    User user;
    Post post;

    @BeforeEach
    void setUp() {
        user = User.builder().username("작성자").build();
        setField(user, "id", 1L);
        post = Post.builder().user(user).build();
        setField(post, "id", 1L);
    }

    @Nested
    @DisplayName("Post 안에 있는 모든 댓글 조회 테스트")
    class get_comments_test {

        @Test
        @DisplayName("로그인 하지 않은 상태로 조회할 때")
        void get_comments_test_not_login() {
            // given
            LoginUser loginUser = null;
            List<Comment> commentList = new ArrayList<>();
            List<CommentResponse> responseList;

            for (int i = 0; i < 3; i++) {
                Comment comment = Comment.builder().content("댓글 내용 " + i).user(user).post(post)
                    .build();
                commentList.add(comment);
            }

            given(commentRepository.findAllByPostId(post.getId())).willReturn(commentList);

            // when
            responseList = commentService.getComments(post.getId(), loginUser);

            // then
            assertThat(responseList).hasSize(3);
            assertThat(responseList.get(0).content()).isEqualTo("댓글 내용 0");
            assertThat(responseList.get(1).content()).isEqualTo("댓글 내용 1");
            assertThat(responseList.get(2).content()).isEqualTo("댓글 내용 2");
        }

        @Test
        @DisplayName("로그인 한 상태로 조회할 때")
        void get_comments_test_login() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);
            User user1 = User.builder().username("댓글 보는 사람").build();
            setField(user1, "id", 2L);

            List<CommentResponse> responseList = new ArrayList<>();
            List<Comment> commentList = new ArrayList<>();
            List<CommentLike> commentLikeList = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                Comment comment = Comment.builder().content("댓글 내용 " + i).user(user).post(post)
                    .build();
                commentList.add(comment);
                CommentLike commentLike = CommentLike.builder().comment(comment).user(user1)
                    .build();
                if (i % 2 == 0) {
                    commentLikeList.add(commentLike);
                }
            }

            given(commentRepository.findAllByPostId(post.getId())).willReturn(commentList);
            given(commentLikeRepoisoty.findByUserId(loginUser.id())).willReturn(commentLikeList);

            responseList = commentService.getComments(post.getId(), loginUser);

            assertThat(responseList).hasSize(3);
            assertThat(responseList.get(0).likeStatus()).isEqualTo(true);
            assertThat(responseList.get(1).likeStatus()).isEqualTo(false);
            assertThat(responseList.get(2).likeStatus()).isEqualTo(true);
        }

    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class create_comments_test {

        @Test
        @DisplayName("댓글 작성 성공 테스트")
        void create_comments_test_success() {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            given(userRepository.findById(loginUser.id())).willReturn(Optional.of(user));
            given(postRepository.findById(request.postId())).willReturn(Optional.of(post));

            CommentResponse response = commentService.createComments(loginUser, request);

            assertThat(response.content()).isEqualTo("댓글 내용");
            assertThat(response.writer()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("댓글 작성 실패 테스트 - user가 존재하지 않을 때")
        void create_comments_test_fail_not_exist_user_exception() {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            given(userRepository.findById(loginUser.id())).willThrow(
                new ServiceException(NOT_EXIST_USER));

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.createComments(loginUser, request));

            assertThat(exception.getCode().getMessage()).isEqualTo("사용자가 없습니다.");
            assertThat(exception.getCode().getCode()).isEqualTo("1000");
        }

        @Test
        @DisplayName("댓글 작성 실패 테스트 - 게시물이 존재하지 않을 때")
        void create_comments_test_fail_not_exist_post() {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            given(userRepository.findById(loginUser.id())).willReturn(Optional.of(user));
            given(postRepository.findById(request.postId())).willThrow(
                new ServiceException(NOT_EXIST_POST));

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.createComments(loginUser, request));

            assertThat(exception.getCode().getMessage()).isEqualTo("게시물이 없습니다.");
            assertThat(exception.getCode().getCode()).isEqualTo("2000");
        }
    }
}