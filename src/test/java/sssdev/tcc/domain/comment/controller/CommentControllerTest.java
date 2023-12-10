package sssdev.tcc.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sssdev.tcc.global.execption.ErrorCode.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.request.CommentModifyRequest;
import sssdev.tcc.domain.comment.dto.request.CommentRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.domain.UserRole;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;
import sssdev.tcc.global.util.StatusUtil;
import sssdev.tcc.support.ControllerTest;

class CommentControllerTest extends ControllerTest {

    @MockBean
    CommentService commentService;

    @MockBean
    StatusUtil statusUtil;

    @Nested
    @DisplayName("게시물 내 댓글 전체 조회")
    class getComments {

        @Test
        @DisplayName("로그인을 하지 않았을 때 전체 조회 할 경우")
        void get_comments_test_not_login() throws Exception {

            CommentRequest request = new CommentRequest(1L);

            List<CommentResponse> responseList = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                CommentResponse response = new CommentResponse("작성자", "댓글 내용 " + i, false);
                responseList.add(response);
            }

            String json = objectMapper.writeValueAsString(request);

            given(statusUtil.isLogin(any())).willReturn(false);
            given(commentService.getComments(request.postId(), null)).willReturn(responseList);

            mockMvc.perform(get("/api/comments")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.size()").value(3),
                    jsonPath("$.data[0].content").value("댓글 내용 0"),
                    jsonPath("$.data[1].content").value("댓글 내용 1"),
                    jsonPath("$.data[2].content").value("댓글 내용 2")
                );
        }

        @Test
        @DisplayName("로그인을 했을 때 전체 조회 할 경우")
        void get_comments_test_login() throws Exception {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);

            User user = User.builder().username("작성자").build();
            setField(user, "id", 1L);

            List<CommentResponse> responseList = new ArrayList<>();
            CommentRequest request = new CommentRequest(1L);

            for (int i = 0; i < 3; i++) {
                CommentResponse response = new CommentResponse(user.getUsername(), "댓글 내용 " + i,
                    i % 2 == 0);
                responseList.add(response);
            }

            String json = objectMapper.writeValueAsString(request);

            given(statusUtil.isLogin(any())).willReturn(true);
            given(statusUtil.getLoginUser(any())).willReturn(loginUser);
            given(commentService.getComments(any(), any())).willReturn(responseList);

            mockMvc.perform(
                    get("/api/comments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.size()").value(3),
                    jsonPath("$.data[0].likeStatus").value(true),
                    jsonPath("$.data[1].likeStatus").value(false),
                    jsonPath("$.data[2].likeStatus").value(true)
                );
        }
    }

    @Nested
    @DisplayName("게시물 내 댓글 생성")
    class createComments {

        @Test
        @DisplayName("성공 테스트")
        void create_comments_success() throws Exception {
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            User user = User.builder().username("작성자").build();
            setField(user, "id", 1L);

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            String json = objectMapper.writeValueAsString(request);

            CommentResponse response = new CommentResponse(user.getUsername(), request.content(),
                false);

            given(commentService.createComments(any(), any())).willReturn(response);

            mockMvc.perform(
                    post("/api/comments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("댓글 생성 성공"),
                    jsonPath("$.data.writer").value("작성자")
                );
        }

        @Test
        @DisplayName("실패 테스트 - User가 존재하지 않을 때")
        void create_comments_fail_test_not_exist_user() throws Exception {

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            String json = objectMapper.writeValueAsString(request);

            given(commentService.createComments(any(), any())).willThrow(
                new ServiceException(NOT_EXIST_USER));

            mockMvc.perform(
                    post("/api/comments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                    jsonPath("$.code").value("1000"),
                    jsonPath("$.message").value("사용자가 없습니다.")
                );
        }

        @Test
        @DisplayName("실패 테스트 - 게시물이 존재하지 않을 때")
        void create_comments_fail_test_not_exist_post() throws Exception {

            CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
            String json = objectMapper.writeValueAsString(request);

            given(commentService.createComments(any(), any())).willThrow(
                new ServiceException(NOT_EXIST_POST));

            mockMvc.perform(
                    post("/api/comments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isNotFound(),
                    jsonPath("$.code").value("2000"),
                    jsonPath("$.message").value("게시글이 없습니다.")
                );
        }
    }

    @Nested
    @DisplayName("댓글 내용 수정")
    class modifyComments {

        @Test
        @DisplayName("댓글 내용 수정 기능 성공 테스트")
        void modify_comments_test_success() throws Exception {
            Long commentId = 1L;
            LoginUser loginUser = new LoginUser(1L, UserRole.USER);
            CommentModifyRequest request = new CommentModifyRequest("바뀐 댓글 내용");
            String json = objectMapper.writeValueAsString(request);
            CommentResponse response = new CommentResponse("작성자", request.content(), false);
            given(commentService.modifyComments(any(), any(), any())).willReturn(response);

            mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andDo(print())
                .andExpectAll(status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("댓글 수정 성공"),
                    jsonPath("$.data.content").value("바뀐 댓글 내용")
                );
        }
    }
}