package sssdev.tcc.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.service.CommentService;
import sssdev.tcc.support.ControllerTest;

class CommentControllerTest extends ControllerTest {

    @MockBean
    CommentService commentService;

    @Test
    @DisplayName("게시물 내 댓글 전체 조회 - 비로그인 시")
    void get_comments_test() throws Exception {

        List<CommentResponse> responseList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            CommentResponse response = new CommentResponse("작성자", "댓글 내용 " + i, null);
            responseList.add(response);
        }

        given(commentService.getComments(any(), any())).willReturn(responseList);

        String json = objectMapper.writeValueAsString(responseList);

        mockMvc.perform(get("/api/comments"))
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