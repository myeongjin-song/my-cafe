package dev.rumble.cafe.service.user.impl;

import dev.rumble.cafe.exception.DuplicatedUserException;
import dev.rumble.cafe.exception.UserNotFoundException;
import dev.rumble.cafe.exception.ValidationException;
import dev.rumble.cafe.model.entity.User;
import dev.rumble.cafe.repository.UserRepository;
import dev.rumble.cafe.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User register(User user) {
        userRepository.findDuplicateUser(user.getName(), user.getPhone(), user.getGender(), user.getBirthDate())
                .ifPresent(existingUser -> {
                    throw new DuplicatedUserException("A user with these details already exists.");
                });
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.isDeleted()){
            throw new RuntimeException("Already deleted!");
        }
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.isDeleted()){
            throw new ValidationException("Already restored!");
        }
        if (user.isDeleted() && user.getDeletedAt().isAfter(LocalDateTime.now().minusDays(30))) {
            user.setDeleted(false);
            user.setDeletedAt(null);
            userRepository.save(user);
        }
    }
}
