package dev.rumble.cafe.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class UserRegistrationDto {
    @NotBlank(message = "Name is mandatory")
    @Schema(description = "이름", example = "Song Myeongjin")
    private String name;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "Invalid phone number")
    @Schema(description = "전화번호", example = "010-1234-1234")
    private String phone;

    @NotBlank(message = "Gender is mandatory")
    @Schema(description = "성별", example = "Male")
    private String gender;

    @NotNull(message = "Birth date is mandatory")
    @Schema(description = "생년월일", example = "1994-03-18")
    private LocalDate birthDate;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
