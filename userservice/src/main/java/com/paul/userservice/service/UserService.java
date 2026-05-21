package com.paul.userservice.service;

import com.paul.userservice.data.dto.UserRequestDto;
import com.paul.userservice.data.dto.UserResponseDto;
import com.paul.userservice.data.entities.User;
import com.paul.userservice.data.exception.AppInfoException;
import com.paul.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new AppInfoException("User with: " + userRequestDto.getEmail() + " already exists", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .address(userRequestDto.getAddress())
                .energyAlertingThreshold(userRequestDto.getEnergyAlertingThreshold())
                .isAlerting(userRequestDto.isAlerting())
                .build();
        userRepository.save(user);
        return buildUserResponseDto(user);
    }

    private UserResponseDto buildUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .energyAlertingThreshold(user.getEnergyAlertingThreshold())
                .isAlerting(user.isAlerting())
                .build();
    }

    public UserResponseDto getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppInfoException("User with id: " + id + " does not exist", HttpStatus.NOT_FOUND);
        }
        return buildUserResponseDto(optionalUser.get());
    }

    public void deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppInfoException("User with id: " + id + " does not exist", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(optionalUser.get());
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new AppInfoException("User with id: " + id + " does not exist", HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        Optional<User> userWithSameEmail = userRepository.findByEmail(userRequestDto.getEmail());

        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
            throw new AppInfoException(
                    "User with: " + userRequestDto.getEmail() + " already exists",
                    HttpStatus.CONFLICT
            );
        }

        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setAddress(userRequestDto.getAddress());
        user.setEnergyAlertingThreshold(userRequestDto.getEnergyAlertingThreshold());
        user.setAlerting(userRequestDto.isAlerting());

        User updatedUser = userRepository.save(user);

        return buildUserResponseDto(updatedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::buildUserResponseDto).toList();
    }
}
