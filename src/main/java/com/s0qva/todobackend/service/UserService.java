package com.s0qva.todobackend.service;

import com.s0qva.todobackend.dto.user.UserNameOnlyUpdatingDto;
import com.s0qva.todobackend.dto.user.UserReadingDto;
import com.s0qva.todobackend.exception.NoSuchUserException;
import com.s0qva.todobackend.mapper.user.UserMapper;
import com.s0qva.todobackend.model.User;
import com.s0qva.todobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, @Qualifier("defaultUserMapper") UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserReadingDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::mapFromUserToUserReadingDto)
                .collect(Collectors.toList());
    }

    public UserReadingDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("There is no user with id = " + id));
        return userMapper.mapFromUserToUserReadingDto(user);
    }

    public UserReadingDto updateUsername(Long id, UserNameOnlyUpdatingDto userNameOnlyUpdatingDto) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("There is no user with id = " + id));
        User newUser = userMapper.mapFromUserNameOnlyUpdatingDtoToUser(userNameOnlyUpdatingDto);

        oldUser.setUsername(newUser.getUsername());
        User savedUser = userRepository.save(oldUser);

        return userMapper.mapFromUserToUserReadingDto(savedUser);
    }
}
