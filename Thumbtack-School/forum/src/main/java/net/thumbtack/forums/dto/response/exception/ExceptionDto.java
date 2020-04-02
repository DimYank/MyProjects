package net.thumbtack.forums.dto.response.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ExceptionDto {

    private List<ErrorDto> errors;

    public ExceptionDto(List<ErrorDto> errors) {
        this.errors = errors;
    }
}
