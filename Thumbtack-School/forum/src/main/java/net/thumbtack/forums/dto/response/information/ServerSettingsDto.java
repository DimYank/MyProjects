package net.thumbtack.forums.dto.response.information;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerSettingsDto {
    private Integer maxBanCount;
    private Integer banTime;
    private int maxNameLength;
    private int minPasswordLength;

    public ServerSettingsDto(Integer maxBanCount, Integer banTime, int maxNameLength, int minPasswordLength) {
        this.maxBanCount = maxBanCount;
        this.banTime = banTime;
        this.maxNameLength = maxNameLength;
        this.minPasswordLength = minPasswordLength;
    }
}
