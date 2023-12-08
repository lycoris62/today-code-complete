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
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.user.domain.User;

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
        post = Post.builder().user(user).build();
        setField(post, "id", 1L);
    }

    @Nested
    @DisplayName("Post 안에 있는 모든 댓글 조회 테스트")
    class get_comments_test {

        @Test
        @DisplayName("로그인 하지 않은 상태로 조회할 때")
        void get_comments_test_not_login() {
            List<Comment> commentList = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                Comment comment = Comment.builder().content("댓글 내용 " + i).user(user).post(post)
                    .build();
                commentList.add(comment);
            }

            given(commentRepository.findAllByPostId(post.getId())).willReturn(commentList);

            List<CommentResponse> commentResponseList = commentService.getCommentsNonLogin(
                post.getId());
            assertThat(commentResponseList).hasSize(4);
            assertThat(commentResponseList.get(0).content()).isEqualTo("댓글 내용 0");
            assertThat(commentResponseList.get(1).content()).isEqualTo("댓글 내용 1");
            assertThat(commentResponseList.get(2).content()).isEqualTo("댓글 내용 2");
        }
    }

}