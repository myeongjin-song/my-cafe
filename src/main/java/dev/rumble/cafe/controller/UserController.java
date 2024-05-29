package dev.rumble.cafe.controller;

import dev.rumble.cafe.model.dto.ApiResult;
import dev.rumble.cafe.model.dto.UserRegistrationDto;
import dev.rumble.cafe.model.dto.UserRegistrationResDto;
import dev.rumble.cafe.model.entity.User;
import dev.rumble.cafe.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static dev.rumble.cafe.model.dto.ApiResult.OK;

@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @Operation(summary = "사용자 등록", description = "새로운 사용자 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"success\": true,\n" +
                                    "  \"response\": {\n" +
                                    "    \"userId\": 1,\n" +
                                    "    \"name\": \"Song Myeongjin\",\n" +
                                    "    \"gender\": \"Male\",\n" +
                                    "    \"birthDate\": \"1994-03-18\",\n" +
                                    "    \"phone\": \"010-1234-1234\"\n" +
                                    "  },\n" +
                                    "  \"error\": null\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"success\": false,\n" +
                                    "  \"response\": null,\n" +
                                    "  \"error\": {\n" +
                                    "    \"status\": 400\n" +
                                    "  }\n" +
                                    "}")))
    })
    @PostMapping("/register")
    public ApiResult<UserRegistrationResDto> register(@RequestBody @Valid UserRegistrationDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setGender(userDto.getGender());
        user.setBirthDate(userDto.getBirthDate());

        User registedUser = userService.register(user);
        return OK(new UserRegistrationResDto(registedUser.getId(), registedUser.getName(), registedUser.getGender(), registedUser.getBirthDate(), registedUser.getPhone()));
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 삭제 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"success\": false,\n" +
                                    "  \"response\": null,\n" +
                                    "  \"error\": {\n" +
                                    "    \"message\": \"User not found\",\n" +
                                    "    \"status\": 404\n" +
                                    "  }\n" +
                                    "}")))
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "사용자 복원", description = "삭제된 사용자를 복원합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 복원 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"success\": false,\n" +
                                    "  \"response\": null,\n" +
                                    "  \"error\": {\n" +
                                    "    \"message\": \"User not found\",\n" +
                                    "    \"status\": 404\n" +
                                    "  }\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "이미 복원된 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"success\": false,\n" +
                                    "  \"response\": null,\n" +
                                    "  \"error\": {\n" +
                                    "    \"message\": \"Already restored!\",\n" +
                                    "    \"status\": 400\n" +
                                    "  }\n" +
                                    "}")))
    })
    @PostMapping("/restore/{id}")
    public void restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
    }
}
