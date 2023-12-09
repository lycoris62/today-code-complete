package sssdev.tcc.domain.comment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.comment.domain.CommentLike;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.global.common.dto.LoginUser;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepoisoty commentLikeRepoisoty;

    public List<CommentResponse> getComments(Long postId, LoginUser loginUser) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        List<CommentResponse> responseList = new ArrayList<>();
        CommentResponse response;
        List<CommentLike> commentLikeList = new ArrayList<>();
        if(loginUser != null){
            commentLikeList = commentLikeRepoisoty.findByUserId(loginUser.id());
        }

        for (Comment comment : commentList) {

            boolean likeStatus = false;

            if(!commentLikeList.isEmpty()){
                for (CommentLike commentLike : commentLikeList) {
                    if(commentLike.getComment().equals(comment)){
                        likeStatus = true;
                        break;
                    }
                }
            }
            response = new CommentResponse(comment.getUser().getUsername(), comment.getContent(), likeStatus);
            responseList.add(response);
        }
        return responseList;
    }


    // todo
    public void deleteComment(Long id) {
    }

    // todo
    public void deletePost(Long id) {

    }
}
