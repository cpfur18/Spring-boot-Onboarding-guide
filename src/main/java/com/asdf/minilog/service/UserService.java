package com.asdf.minilog.service;

import com.asdf.minilog.dto.UserRequestDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 클래스 내 모든 메서드에 트랜잭션 적용 해 데이터 변경 작업이 안전하게 이루어지도록 보장
// 예외 발생 시 자동 롤백 수행
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // readOnly = true로 쓰기 잠금을 생략해 리소스 절약 + 조회성능 향상
    // 종료 시 변경 내용을 커밋하지 않도록 처해 원치 않는 데이터 변경 방지
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    // 단건은 Optional, 다수는 Stream
    public Optional<UserResponseDto> getUserById(Long userId) {
        return userRepository.findById(userId).map(EntityDtoMapper::toDto);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            // IllegalArgumentException : 인수가 유효하지 않음
            throw new IllegalArgumentException("이미 존재하는 사용자 이름 입니다.");
        }

        User saveUser =
                userRepository.save(
                        User.builder()
                                .username(userRequestDto.getUsername())
                                .password(userRequestDto.getPassword())
                                .build());
        return EntityDtoMapper.toDto(saveUser);
    }

    // RequestDTO에 ID를 안 넣고 따로 받는 이유 = 역할분리
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user =
                userRepository
                        .findById(userId)
                        // orElseThrow는 값이 없을 때 예외처리 -> 람다를 사용해 필요할 때만 생성하여 리소스 절약
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 " + "사용자를 찾을 수 없습니다.",
                                                        userId)));
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(userRequestDto.getPassword());

        // var는 로컬 선언 시 사용 가능한 타입 식별자, 컴파일러가 결정(변수 선언 시 타입 생략 가능)
        // JPA의 Dirty Checking에 의해 setUsername, setPassword이 되어 save를 사용하지 않아도 update됨
        //         var updatedUser = userRepository.save(user);
        //         return EntityDtoMapper.toDto(updatedUser);
        return EntityDtoMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 사용자를 찾을 수" + "없습니다.",
                                                        userId)));
        userRepository.delete(user);
    }
}
