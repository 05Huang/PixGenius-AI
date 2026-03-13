package cn.hx.pix.genius.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码验证响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordVerifyResponse {
    /**
     * 验证是否成功
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
}

