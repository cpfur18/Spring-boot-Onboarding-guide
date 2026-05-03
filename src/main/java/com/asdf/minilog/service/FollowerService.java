package com.asdf.minilog.service;

import com.asdf.minilog.dto.FollowResponseDto;
import com.asdf.minilog.entity.Follow;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.FollowerRepository;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowerService(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

   public FollowResponseDto follow(Long follwerId, Long follweeId) {
        if (follwerId.equals(follweeId)) {
            throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
        }

        User follower =
                userRepository
                        .findById(follwerId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("팔로어 아이디(%d)를 가진 사용자를 찾을 수" +
                                                        " 없습니다.", follwerId)));
        User followee =
                userRepository
                        .findById(follweeId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("팔로잉 아이디(%d)를 가진 사용자를 찾을 수 " +
                                                        "없습니다.")));

        Follow follow =
                followerRepository.save(EntityDtoMapper.toEntity(follwerId, follweeId));
        return EntityDtoMapper.toDto(follow);

    }

}
