package cn.hx.pix.genius.core.dto;

import lombok.Data;

@Data
public class UserLoginResponeDto {

    Long id;
    String name;
    String avatar;
    String token;
}
