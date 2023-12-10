package sssdev.tcc.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.support.RepositoryTest;

class CommentRepositoryTest extends RepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder().description("").nickname("")
            .username("").profileUrl("").build();
        setField(user, "id", 1L);
        Post post = Post.builder().content("").user(user).build();
        setField(post, "id", 1L);

        userRepository.save(user);
        postRepository.save(post);

        for (int i = 0; i < 10; i++) {
            Comment comment = Comment.builder()
                .content("댓글내용 " + i)
                .user(user)
                .post(post)
                .build();
            commentRepository.save(comment);
        }
    }

    @Test
    @DisplayName("해당 Post에 들어있는 모든 댓글을 조회하는 기능 테스트")
    void findAllByPostIdTest() {
        List<Comment> commentList = commentRepository.findAllByPostId(1L);

        assertThat(commentList).hasSize(10);
        assertThat(commentList.get(0).getContent()).isEqualTo("댓글내용 0");
    }
}