package dev.rumble.cafe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rumble.cafe.exception.DuplicatedUserException;
import dev.rumble.cafe.exception.ProductOutOfStockException;
import dev.rumble.cafe.exception.UserNotFoundException;
import dev.rumble.cafe.exception.ValidationException;
import dev.rumble.cafe.model.dto.UserRegistrationDto;
import dev.rumble.cafe.model.entity.User;
import dev.rumble.cafe.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("성공케이스 - 사용자 등록")
    public void testRegisterUser() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setName("Song Myeongjin");
        userDto.setPhone("010-1234-1234");
        userDto.setGender("Male");
        userDto.setBirthDate(LocalDate.of(1994, 3, 18));

        User user = new User();
        user.setId(1L);
        user.setName("Song Myeongjin");
        user.setPhone("010-1234-1234");
        user.setGender("Male");
        user.setBirthDate(LocalDate.of(1994, 3, 18));

        when(userService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name", is("Song Myeongjin")))
                .andExpect(jsonPath("$.response.phone", is("010-1234-1234")))
                .andExpect(jsonPath("$.response.gender", is("Male")))
                .andExpect(jsonPath("$.response.birthDate", is("1994-03-18")));

    }
    @Test
    @DisplayName("에러케이스 - 전화번호 유효성 실패")
    public void testRegisterUserBadRequest() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setName("Song Myeongjin");
        userDto.setPhone("010-12234-12234");
        userDto.setGender("Male");
        userDto.setBirthDate(LocalDate.of(1994, 3, 18));

        User user = new User();
        user.setId(1L);
        user.setName("Song Myeongjin");
        user.setPhone("010-12234-12234");
        user.setGender("Male");
        user.setBirthDate(LocalDate.of(1994, 3, 18));

        when(userService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 복구 테스트")
    public void testRestoreUser() throws Exception {
        doNothing().when(userService).restoreUser(anyLong());

        mockMvc.perform(post("/users/restore/1"))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("에러케이스 - 사용자 없는 경우")
    public void testRestoreUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).restoreUser(anyLong());

        mockMvc.perform(post("/users/restore/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("에러케이스 - 기복구된 사용자 케이스")
    public void testRestoreUserAlreadyRestored() throws Exception {
        doThrow(new ValidationException("Already restored!")).when(userService).restoreUser(anyLong());

        mockMvc.perform(post("/users/restore/1"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("에러케이스 - 중복 사용자 등록 케이스 ")
    public void testDuplicatedUserEnrollment() throws Exception {
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setName("Song Myeongjin");
        userDto.setPhone("010-1234-1234");
        userDto.setGender("Male");
        userDto.setBirthDate(LocalDate.of(1994, 3, 18));

        doThrow(new DuplicatedUserException("A user with these details already exists."))
                .when(userService).register(any(User.class));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }
}
