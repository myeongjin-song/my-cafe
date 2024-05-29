package dev.rumble.cafe.service.user;

import dev.rumble.cafe.model.entity.User;

public interface UserService {
    User register(User user);
    void deleteUser(Long userId);
    void restoreUser(Long userId);
}
