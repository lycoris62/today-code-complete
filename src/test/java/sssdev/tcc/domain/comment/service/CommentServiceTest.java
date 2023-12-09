package sssdev.tcc.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.ArrayList;
import java.util.List;
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
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.common.dto.LoginUser;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentLikeRepoisoty commentLikeRepoisoty;

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
                Comment comment = Comment.builder().content("댓글 내용 " + i).user(user).post(post).build();
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
                Comment comment = Comment.builder().content("댓글 내용 " + i).user(user).post(post).build();
                commentList.add(comment);
                CommentLike commentLike = CommentLike.builder().comment(comment).user(user1).build();
                if(i % 2 == 0)
                    commentLikeList.add(commentLike);
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
}