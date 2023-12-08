package sssdev.tcc.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.comment.dto.request.CommentRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.global.util.StatusUtil;
import sssdev.tcc.support.ControllerTest;

class CommentControllerTest extends ControllerTest {

    @MockBean
    CommentService commentService;

    @MockBean
    StatusUtil statusUtil;

    @Nested
    @DisplayName("게시물 내 댓글 전체 조회")
    class get_comments_test {

        @Test
        @DisplayName("로그인을 하지 않았을 때 전체 조회 할 경우")
        void get_comments_test() throws Exception {

            CommentRequest request = new CommentRequest(1L);

            List<CommentResponse> responseList = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                CommentResponse response = new CommentResponse("작성자", "댓글 내용 " + i, null);
                responseList.add(response);
            }

            String json = objectMapper.writeValueAsString(request);

            given(statusUtil.loginStatus(any(HttpServletRequest.class))).willReturn(true);
            given(commentService.getCommentsNonLogin(any())).willReturn(responseList);

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


    }
}