package cn.hx.pix.genius.core.dto;

import lombok.Data;

/**
 * 密码验证请求DTO
 */
@Data
public class PasswordVerifyRequest {
    /**
     * 访问密码
     */
    private String password;
}

