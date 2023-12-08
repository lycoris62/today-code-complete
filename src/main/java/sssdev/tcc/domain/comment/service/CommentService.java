package sssdev.tcc.domain.comment.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.global.common.dto.LoginUser;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepoisoty commentLikeRepoisoty;

    public List<CommentResponse> getCommentsNonLogin(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        List<CommentResponse> responseList = new ArrayList<>();

        for (Comment comment : commentList) {
            CommentResponse commentResponse = CommentResponse.builder()
                .writer(comment.getUser().getUsername())
                .content(comment.getContent())
                .build();
            responseList.add(commentResponse);
        }

        return responseList;
    }

    public List<CommentResponse> getCommentsLogin(Long postId, LoginUser loginUser) {
        return null;
    }
}
