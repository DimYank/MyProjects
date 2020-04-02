package net.thumbtack.forums.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class Settings {
    @Value("${max_ban_count}")
    private int maxBanCount;
    @Value("${ban_time}")
    private int banTime;
    @Value("${max_name_length}")
    private int maxNameLength;
    @Value("${min_password_length}")
    private int minPasswordLength;
    @Value("${rest_http_port}")
    private int restHttpPort;
}
