package dev.rumble.cafe.model.dto;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class ApiError {
    @Schema(description = "에러 메시지", example = "Validation Error")
    private final String message;
    @Schema(description = "에러코드", example = "400")
    private final int status;
    ApiError(Throwable throwable, HttpStatus status) {
        this(throwable.getMessage(), status);
    }

    ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
