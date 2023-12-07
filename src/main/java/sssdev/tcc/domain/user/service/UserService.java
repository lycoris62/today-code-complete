package sssdev.tcc.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sssdev.tcc.domain.user.dto.response.ProfileResponse;
import sssdev.tcc.domain.user.repository.FollowRepository;
import sssdev.tcc.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileResponse getProfile(Long id) {
        var user = userRepository.findById(id).orElseThrow();
        return ProfileResponse.of(user, followRepository);
    }
}
