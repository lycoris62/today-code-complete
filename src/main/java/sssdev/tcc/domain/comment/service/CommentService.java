package sssdev.tcc.domain.comment.service;

import static sssdev.tcc.global.execption.ErrorCode.CHECK_USER;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_COMMENT;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_POST;
import static sssdev.tcc.global.execption.ErrorCode.NOT_EXIST_USER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sssdev.tcc.domain.admin.dto.request.AdminCommetUpdateRequest;
import sssdev.tcc.domain.admin.dto.response.AdminCommentUpdateResponse;
import sssdev.tcc.domain.comment.domain.Comment;
import sssdev.tcc.domain.comment.domain.CommentLike;
import sssdev.tcc.domain.comment.dto.request.CommentCreateRequest;
import sssdev.tcc.domain.comment.dto.request.CommentModifyRequest;
import sssdev.tcc.domain.comment.dto.response.CommentResponse;
import sssdev.tcc.domain.comment.repository.CommentLikeRepoisoty;
import sssdev.tcc.domain.comment.repository.CommentRepository;
import sssdev.tcc.domain.post.domain.Post;
import sssdev.tcc.domain.post.repository.PostRepository;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.common.dto.LoginUser;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepoisoty commentLikeRepoisoty;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

    public CommentResponse createComments(LoginUser loginUser, CommentCreateRequest requestDto) {
        CommentResponse response;

        User user = userRepository.findById(loginUser.id()).orElseThrow(
            () -> new ServiceException(ErrorCode.NOT_EXIST_USER)
        );

        Post post = postRepository.findById(requestDto.postId()).orElseThrow(
            () -> new ServiceException(ErrorCode.NOT_EXIST_POST)
        );

        Comment comment = Comment.builder()
            .content(requestDto.content())
            .user(user)
            .post(post)
            .build();

        response = CommentResponse.builder()
            .content(comment.getContent())
            .writer(comment.getUser().getUsername())
            .likeStatus(false)
            .build();

        commentRepository.save(comment);
        return response;
    }

    // todo
    public void deleteCommentAdmin(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));
        commentRepository.delete(comment);
    }

    // todo
    public AdminCommentUpdateResponse updateCommentAdmin(Long id,
        AdminCommetUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ServiceException(NOT_EXIST_POST));
        return AdminCommentUpdateResponse.builder().id(id).content(request.content()).build();
    }

    @Transactional
    public CommentResponse modifyComments(Long id, CommentModifyRequest request, LoginUser loginUser) {
        Comment comment = commentRepository.findById(id).orElseThrow(
            () -> new ServiceException(NOT_EXIST_COMMENT)
        );

        User user = userRepository.findById(loginUser.id()).orElseThrow(
            () -> new ServiceException(NOT_EXIST_USER)
        );

        boolean likeStatus = false;

        if(!comment.getUser().getId().equals(user.getId())) {
            throw new ServiceException(CHECK_USER);
        }

        comment.updateComment(request.content());

        return new CommentResponse(user.getUsername(), comment.getContent(), likeStatus);
    }
}
