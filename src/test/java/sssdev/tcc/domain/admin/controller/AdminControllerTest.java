package sssdev.tcc.domain.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.admin.dto.request.AdminCommentUpdateRequest;
import sssdev.tcc.domain.admin.dto.request.AdminPostUpdateRequest;
import sssdev.tcc.domain.admin.dto.request.AdminUserUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminCommentUpdateResponse;
import sssdev.tcc.domain.admin.dto.response.AdminPostUpdateResponse;
import sssdev.tcc.domain.admin.dto.response.AdminUserUpdateResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.domain.post.service.PostService;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.domain.user.service.UserService;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.util.StatusUtil;
import sssdev.tcc.support.ControllerTest;


@DisplayName("관리자 API")
class AdminControllerTest extends ControllerTest {

    @MockBean
    UserService userService;
    @MockBean
    PostService postService;
    @MockBean
    CommentService commentService;
    @MockBean
    StatusUtil statusUtil;

    @DisplayName("사용자 정보 수정 테스트")
    @Nested
    class ProfileUpdate {

        @DisplayName("성공")
        @Test
        void admin_profile_update_success() throws Exception {
            // given
            var request = AdminUserUpdateRequest.builder()
                .profileUrl("/api/test.png")
                .role(UserRole.ADMIN)
                .nickname("test nick")
                .description("test description")
                .userId(2L)
                .build();

            var response = AdminUserUpdateResponse.builder()
                .profileUrl(request.profileUrl())
                .role(request.role())
                .nickname(request.nickname())
                .description(request.description())
                .userId(request.userId())
                .build();

            var loginUser = LoginUser.builder()
                .role(UserRole.ADMIN)
                .id(1L)
                .build();

            given(statusUtil.getLoginUser(any())).willReturn(loginUser);
            given(userService.updateProfileAdmin(any(AdminUserUpdateRequest.class)))
                .willReturn(response);
            // when // then
            mockMvc.perform(patch("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("성공했습니다."),
                    jsonPath("$.data.nickname").value(response.nickname()),
                    jsonPath("$.data.role").value(response.role().toString()),
                    jsonPath("$.data.description").value(response.description()),
                    jsonPath("$.data.profileUrl").value(response.profileUrl())
                );
        }
    }


    @DisplayName("게시글 삭제 테스트")
    @Nested
    class PostDelete {

        @DisplayName("성공")
        @Test
        void delete_post_success() throws Exception {
            // given
            var postId = 1L;
            // when // then
            mockMvc.perform(delete("/api/admin/posts/{id}", postId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("성공했습니다.")
                );
        }
    }

    @DisplayName("게시글 수정 테스트")
    @Nested
    class PostUpdate {

        @DisplayName("성공")
        @Test
        void post_update_success() throws Exception {
            // given
            var userId = 1L;
            var postId = 1L;
            var request = AdminPostUpdateRequest.builder()
                .userId(userId)
                .content("change content")
                .build();
            var response = AdminPostUpdateResponse.builder()
                .id(postId)
                .content(request.content())
                .build();

            given(postService.updatePostAdmin(eq(postId), any())).willReturn(response);
            // when // then
            mockMvc.perform(patch("/api/admin/posts/{id}", postId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("성공했습니다."),
                    jsonPath("$.data.content").value(response.content())
                );
        }
    }


    @DisplayName("댓글 수정 테스트")
    @Nested
    class CommentUpdate {

        @DisplayName("성공")
        @Test
        void comment_update_success() throws Exception {
            // given
            var userId = 1L;
            var postId = 1L;
            var request = AdminCommentUpdateRequest.builder()
                .userId(userId)
                .content("change content")
                .build();
            var response = AdminCommentUpdateResponse.builder()
                .id(postId)
                .content(request.content())
                .build();

            given(commentService.updateCommentAdmin(eq(postId), any())).willReturn(response);
            // when // then
            mockMvc.perform(patch("/api/admin/comments/{id}", postId)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("성공했습니다."),
                    jsonPath("$.data.content").value(response.content())
                );
        }
    }

    @DisplayName("댓글 삭제 테스트")
    @Nested
    class CommentDelete {

        @DisplayName("성공")
        @Test
        void delete_comment_success() throws Exception {
            // given
            var commentId = 1L;
            // when // then
            mockMvc.perform(delete("/api/admin/comments/{id}", commentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("성공했습니다.")
                );
        }
    }
}
