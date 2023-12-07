package sssdev.tcc.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sssdev.tcc.domain.user.domain.User;
import sssdev.tcc.domain.user.dto.request.UserFollowRequest;
import sssdev.tcc.domain.user.dto.request.UserFollowResponse;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;
import sssdev.tcc.global.execption.ErrorCode;
import sssdev.tcc.global.execption.ServiceException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileResponse getProfile(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        return ProfileResponse.of(user, followRepository);
    }

    public UserFollowResponse follow(UserFollowRequest request) {
        User from = userRepository.findById(request.fromUserId())
            .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        User to = userRepository.findById(request.toUserId())
            .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));

        from.follow(to);
        return new UserFollowResponse(
            to.getId(),
            to.getFollowerCount(followRepository),
            to.getFollowingCount(followRepository)
        );
    }
}
