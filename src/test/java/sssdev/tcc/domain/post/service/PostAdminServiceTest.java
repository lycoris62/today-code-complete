package sssdev.tcc.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_POST;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sssdev.tcc.domain.admin.dto.request.AdminPostUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminPostUpdateResponse;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PostAdminServiceTest {

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;


    @Nested
    @DisplayName("게시글 수정")
    class UpdatePost {

        @DisplayName("성공 케이스 - 게시글 수정 성공")
        @Test
        void update_post_success() {
            // given
            User user = User.builder().username("username01").build();
            setField(user, "id", 1L);
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);

            AdminPostUpdateRequest requestDto = new AdminPostUpdateRequest(1L, "content02");

            given(postRepository.findById(post1.getId())).willReturn(Optional.of(post1));

            // when
            AdminPostUpdateResponse responseDto = postService
                .updatePostAdmin(post1.getId(), requestDto);

            // then
            assertThat(responseDto.content()).isEqualTo(requestDto.content());
        }

        @DisplayName("실패 케이스 - 없는 게시글")
        @Test
        void update_post_fail_not_exist_post() {
            // given
            User user = User.builder().username("username01").build();
            setField(user, "id", 1L);
            Post post1 = Post.builder().content("content01").user(user).build();
            setField(post1, "id", 1L);

            AdminPostUpdateRequest requestDto = new AdminPostUpdateRequest(1L, "content02");

            given(postRepository.findById(post1.getId()))
                .willThrow(new ServiceException(NOT_EXIST_POST));

            // when && then
            assertThatThrownBy(() -> postService
                .updatePostAdmin(post1.getId(), requestDto))
                .isInstanceOf(ServiceException.class)
                .satisfies(exception -> {
                    ErrorCode errorCode = ((ServiceException) exception).getCode();
                    assertThat(errorCode.getCode()).isEqualTo("2000");
                    assertThat(errorCode.getMessage()).isEqualTo("게시글이 없습니다.");
                    assertThat(errorCode.getStatus()).isEqualTo(NOT_FOUND);
                });
        }

        @Nested
        @DisplayName("게시글 삭제")
        class DeletePost {

            @DisplayName("성공 케이스 - 게시글 삭제 성공")
            @Test
            void delete_post_success() {
                // given
                User user = User.builder().username("username01").build();
                setField(user, "id", 1L);
                Post post1 = Post.builder().content("content01").user(user).build();
                setField(post1, "id", 1L);

                given(postRepository.findById(post1.getId())).willReturn(Optional.of(post1));

                // when
                postService.deletePostAdmin(post1.getId());

                // then
                then(postRepository).should().delete(post1);
            }

            @DisplayName("실패 케이스 - 없는 게시글")
            @Test
            void delete_post_fail_not_exist_post() {
                // given
                User user = User.builder().username("username01").build();
                setField(user, "id", 1L);
                Post post1 = Post.builder().content("content01").user(user).build();
                setField(post1, "id", 1L);

                given(postRepository.findById(post1.getId()))
                    .willThrow(new ServiceException(NOT_EXIST_POST));

                // when && then
                assertThatThrownBy(() -> postService
                    .deletePostAdmin(post1.getId()))
                    .isInstanceOf(ServiceException.class)
                    .satisfies(exception -> {
                        ErrorCode errorCode = ((ServiceException) exception).getCode();
                        assertThat(errorCode.getCode()).isEqualTo("2000");
                        assertThat(errorCode.getMessage()).isEqualTo("게시글이 없습니다.");
                        assertThat(errorCode.getStatus()).isEqualTo(NOT_FOUND);
                    });
            }
        }
    }
}