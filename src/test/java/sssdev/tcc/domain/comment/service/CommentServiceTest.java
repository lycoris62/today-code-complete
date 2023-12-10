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
import sssdev.tcc.domain.comment.dto.request.CommentModifyRequest;
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
            given(userRepository.findById(loginUser.id())).willReturn(Optional.empty());

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
            given(postRepository.findById(request.postId())).willReturn(Optional.empty());

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.createComments(loginUser, request));

            assertThat(exception.getCode().getMessage()).isEqualTo("게시글이 없습니다.");
            assertThat(exception.getCode().getCode()).isEqualTo("2000");
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class modify_comments_test {

        @Test
        @DisplayName("댓글 수정 성공 테스트")
        void modify_connets_test_success() {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            Comment comment = Comment.builder().content("댓글 내용").user(user).post(post).build();
            setField(comment, "id", 1L);

            CommentModifyRequest request = new CommentModifyRequest("수정된 댓글 내용");

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

            CommentResponse response = commentService.modifyComments(comment.getId(), request,
                loginUser);

            assertThat(response.content()).isEqualTo("수정된 댓글 내용");


        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 - 자신의 댓글이 아닐 때")
        void modify_connets_test_fail_this_comment_is_not_mine() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);

            User user2 = User.builder().username("찾는사람").build();
            setField(user, "id", 2L);

            Comment comment = Comment.builder().content("댓글 내용").user(user).post(post).build();
            setField(comment, "id", 1L);

            CommentModifyRequest request = new CommentModifyRequest("수정된 댓글 내용");

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user2));

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.modifyComments(comment.getId(), request, loginUser)
            );

            assertThat(exception.getCode()).isEqualTo(CHECK_USER);
            assertThat(exception.getCode().getMessage()).isEqualTo("본인이 아닙니다.");
        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 - 수정하려는 댓글이 존재하지 않을 때")
        void modify_connets_test_fail_comment_is_not_exist() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);
            CommentModifyRequest request = new CommentModifyRequest("수정된 댓글 내용");
            given(commentRepository.findById(any())).willReturn(Optional.empty());

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.modifyComments(any(), request, loginUser)
            );

            assertThat(exception.getCode()).isEqualTo(NOT_EXIST_COMMENT);
            assertThat(exception.getCode().getMessage()).isEqualTo("댓글이 없습니다.");
        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 - 유저가 존재하지 않을 때")
        void modify_comments_test_fail_user_is_not_exist() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);
            CommentModifyRequest request = new CommentModifyRequest("수정된 댓글 내용");

            Comment comment = Comment.builder().content("댓글 내용").user(user).post(post).build();
            setField(comment, "id", 1L);

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

            given(userRepository.findById(any())).willReturn(Optional.empty());

            ServiceException exception = assertThrows(ServiceException.class,
                () -> commentService.modifyComments(comment.getId(), request, loginUser)
            );

            assertThat(exception.getCode()).isEqualTo(NOT_EXIST_USER);
            assertThat(exception.getCode().getMessage()).isEqualTo("사용자가 없습니다.");
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class delete_comments_test {

        @Test
        @DisplayName("댓글 삭제 성공 테스트")
        void delete_comments_test_success() {
            Long id = 1L;
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);

            Comment comment = Comment.builder().content("삭제할 댓글")
                .user(user).post(post).build();

            given(commentRepository.findById(id)).willReturn(Optional.of(comment));
            given(userRepository.findById(loginUser.id())).willReturn(Optional.of(user));

            commentService.deleteComments(id, loginUser);

            verify(commentRepository, times(1)).delete(any());
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 테스트")
    class like_comments_test {

        @Test
        @DisplayName("댓글 좋아요 성공 테스트")
        void like_comments_test_success() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);
            User user1 = User.builder().username("좋아요 누르는 사람").build();
            setField(user1, "id", 2L);

            Comment comment = Comment.builder().content("좋아요 댓글").user(user).post(post).build();
            setField(comment, "id", 1L);

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

            CommentResponse commentResponse = commentService.likeComments(comment.getId(), loginUser);

            assertThat(commentResponse.likeStatus()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("댓글 좋아요 취소 테스트")
    class like_comments_cancel_test {

        @Test
        @DisplayName("댓글 좋아요 취소 성공 테스트")
        void cancel_like_comments_test_success() {
            LoginUser loginUser = new LoginUser(2L, UserRole.USER);
            User user1 = User.builder().username("좋아요 취소 누르는 사람").build();
            setField(user1, "id", 2L);

            Comment comment = Comment.builder().content("좋아요 댓글").user(user).post(post).build();
            setField(comment, "id", 1L);

            CommentLike commentLike = CommentLike.builder().comment(comment).user(user1).build();

            given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
            given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
            given(commentLikeRepoisoty.findByUserAndComment(user1,comment)).willReturn(commentLike);

            CommentResponse response = commentService.cancelLikeComments(comment.getId(), loginUser);

            verify(commentLikeRepoisoty, times(1)).delete(any());
            assertThat(response.likeStatus()).isEqualTo(false);
        }
    }
}