package dev.rumble.cafe.service;

import dev.rumble.cafe.exception.DuplicatedUserException;
import dev.rumble.cafe.exception.ProductOutOfStockException;
import dev.rumble.cafe.model.entity.User;
import dev.rumble.cafe.repository.UserRepository;
import dev.rumble.cafe.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("성공케이스 - 사용자 등록 테스트")
    public void testRegisterUser() {
        User user = new User();
        user.setName("Song Myeongjin");
        user.setPhone("010-3136-9040");
        user.setGender("Male");
        user.setBirthDate(LocalDate.of(1994, 3, 18));

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.register(user);

        assertNotNull(createdUser);
        assertEquals("Song Myeongjin", createdUser.getName());
        assertEquals("010-3136-9040", createdUser.getPhone());
        assertEquals("Male", createdUser.getGender());
        assertEquals(LocalDate.of(1994, 3, 18), createdUser.getBirthDate());
    }

    @Test
    @DisplayName("성공케이스 - 사용자 삭제 테스트")
    public void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertTrue(user.isDeleted());
    }

    @Test
    @DisplayName("성공케이스 - 사용자 복구 테스트")
    public void testRestoreUser() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now().minusDays(20).atStartOfDay());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.restoreUser(1L);

        assertFalse(user.isDeleted());
        assertNull(user.getDeletedAt());
    }

    @Test
    @DisplayName("에러케이스 - 30일 이후 계정 복구 케이스")
    public void testRestoreUserAfter30Days() {
        User user = new User();
        user.setId(1L);
        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now().minusDays(31).atStartOfDay());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.restoreUser(1L);

        assertTrue(user.isDeleted());
        assertNotNull(user.getDeletedAt());
        verify(userRepository, never()).save(user);
    }

    @Test
    @DisplayName("에러케이스 - 중복 사용자 등록케이스")
    public void testDuplicatedUser(){
        User user = new User();
        user.setName("Song Myeongjin");
        user.setPhone("1234567890");
        user.setGender("Male");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.findDuplicateUser(any(),any(),any(),any())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DuplicatedUserException.class, () -> {
            userService.register(user);
        });

        assertEquals("A user with these details already exists.", exception.getMessage());
    }
}
