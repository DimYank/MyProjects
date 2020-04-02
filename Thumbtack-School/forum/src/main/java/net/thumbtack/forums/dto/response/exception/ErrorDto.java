package net.thumbtack.forums.dto.response.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorDto {
    private String errorCode;
    private String field;
    private String message;

    public ErrorDto(String errorCode, String field, String message) {
        this.errorCode = errorCode;
        this.field = field;
        this.message = message;
    }
}
