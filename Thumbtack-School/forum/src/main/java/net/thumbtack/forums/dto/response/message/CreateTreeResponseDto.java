package net.thumbtack.forums.dto.response.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateTreeResponseDto {
    private long id;

    public CreateTreeResponseDto(long id) {
        this.id = id;
    }
}
