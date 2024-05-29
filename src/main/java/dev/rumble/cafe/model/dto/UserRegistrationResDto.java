package dev.rumble.cafe.model.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
public class UserRegistrationResDto {

    @Schema(description = "UserId" ,example = "1")
    private final Long userId;
    @Schema(description = "사용자명", example = "song myeongjin")
    private final String name;
    @Schema(description = "성별", example = "male")
    private final String gender;
    @Schema(description = "생년월일", example = "1994-03-18")
    private final LocalDate birthDate;
    @Schema(description = "전화번호", example = "010-2134-1232")
    private final String phone;

    public UserRegistrationResDto(Long userId, String name, String gender, LocalDate birthDate, String phone) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public Long getUserId() {
        return userId;
    }
}
