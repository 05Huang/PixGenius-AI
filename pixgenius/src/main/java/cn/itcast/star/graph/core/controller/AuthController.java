package cn.hx.pix.genius.core.controller;

import cn.hx.pix.genius.core.dto.PasswordVerifyRequest;
import cn.hx.pix.genius.core.dto.PasswordVerifyResponse;
import cn.hx.pix.genius.core.dto.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证控制器
 * 处理网站访问密码验证
 */
@Slf4j
@RestController
@RequestMapping("/api/1.0/auth")
public class AuthController {

    @Value("${site.access.password:admin123}")
    private String sitePassword;

    // 记录IP地址的失败尝试次数（简单实现，生产环境建议使用Redis）
    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockoutTime = new ConcurrentHashMap<>();

    // 最大尝试次数
    private static final int MAX_ATTEMPTS = 5;
    // 锁定时长（15分钟）
    private static final long LOCKOUT_DURATION = 15 * 60 * 1000;

    /**
     * 验证网站访问密码
     * 
     * @param request 密码验证请求
     * @param httpRequest HTTP请求对象
     * @return 验证结果
     */
    @PostMapping("/verify-password")
    public Result<PasswordVerifyResponse> verifyPassword(@RequestBody PasswordVerifyRequest request,
                                                          HttpServletRequest httpRequest) {
        String ipAddress = getClientIP(httpRequest);
        log.info("收到密码验证请求，IP: {}", ipAddress);

        // 检查IP是否被锁定
        if (isLocked(ipAddress)) {
            long remainingTime = (lockoutTime.get(ipAddress) + LOCKOUT_DURATION - System.currentTimeMillis()) / 1000;
            log.warn("IP地址被锁定: {}, 剩余时间: {}秒", ipAddress, remainingTime);
            return Result.ok(new PasswordVerifyResponse(false,
                    String.format("尝试次数过多，请在%d秒后重试", remainingTime)));
        }

        // 验证密码是否为空
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("密码为空，IP: {}", ipAddress);
            return Result.ok(new PasswordVerifyResponse(false, "密码不能为空"));
        }

        // 验证密码
        if (sitePassword.equals(request.getPassword())) {
            // 验证成功，清除失败记录
            failedAttempts.remove(ipAddress);
            lockoutTime.remove(ipAddress);
            log.info("密码验证成功, IP: {}", ipAddress);
            return Result.ok(new PasswordVerifyResponse(true, "验证成功"));
        } else {
            // 验证失败，记录失败次数
            int attempts = failedAttempts.getOrDefault(ipAddress, 0) + 1;
            failedAttempts.put(ipAddress, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                // 达到最大尝试次数，锁定IP
                lockoutTime.put(ipAddress, System.currentTimeMillis());
                log.warn("密码验证失败次数过多，IP被锁定: {}, 失败次数: {}", ipAddress, attempts);
                return Result.ok(new PasswordVerifyResponse(false,
                        "尝试次数过多，已锁定15分钟"));
            }

            log.warn("密码验证失败, IP: {}, 失败次数: {}/{}", ipAddress, attempts, MAX_ATTEMPTS);
            return Result.ok(new PasswordVerifyResponse(false,
                    String.format("密码错误，还有%d次机会", MAX_ATTEMPTS - attempts)));
        }
    }

    /**
     * 检查IP是否被锁定
     * 
     * @param ipAddress IP地址
     * @return 是否被锁定
     */
    private boolean isLocked(String ipAddress) {
        Long lockedAt = lockoutTime.get(ipAddress);
        if (lockedAt == null) {
            return false;
        }

        // 检查锁定时间是否已过
        if (System.currentTimeMillis() - lockedAt > LOCKOUT_DURATION) {
            // 锁定时间已过，清除记录
            lockoutTime.remove(ipAddress);
            failedAttempts.remove(ipAddress);
            return false;
        }

        return true;
    }

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于多级代理，取第一个非unknown的IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 清除指定IP的失败记录（管理接口，可选）
     * 
     * @param ip IP地址
     * @return 操作结果
     */
    @PostMapping("/clear-lockout")
    public Result<PasswordVerifyResponse> clearLockout(@RequestParam String ip) {
        failedAttempts.remove(ip);
        lockoutTime.remove(ip);
        log.info("清除IP锁定记录: {}", ip);
        return Result.ok(new PasswordVerifyResponse(true, "已清除锁定记录"));
    }

    /**
     * 获取失败统计信息（管理接口，可选）
     * 
     * @return 统计信息
     */
    @GetMapping("/failed-stats")
    public Result<String> getFailedStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("失败尝试统计:\n");
        failedAttempts.forEach((ip, count) -> {
            sb.append(String.format("IP: %s, 失败次数: %d\n", ip, count));
        });
        sb.append("\n锁定IP列表:\n");
        lockoutTime.forEach((ip, time) -> {
            long remainingTime = (time + LOCKOUT_DURATION - System.currentTimeMillis()) / 1000;
            sb.append(String.format("IP: %s, 剩余锁定时间: %d秒\n", ip, Math.max(0, remainingTime)));
        });
        return Result.ok(sb.toString());
    }
}

