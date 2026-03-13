package cn.hx;

import cn.hx.pix.genius.core.dto.PasswordVerifyRequest;
import cn.hx.pix.genius.core.dto.PasswordVerifyResponse;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * 密码验证测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PasswordVerifyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCorrectPassword() {
        // 测试正确密码
        PasswordVerifyRequest request = new PasswordVerifyRequest();
        request.setPassword("admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PasswordVerifyRequest> entity = new HttpEntity<>(request, headers);

        PasswordVerifyResponse response = restTemplate.postForObject(
                "/api/1.0/auth/verify-password",
                entity,
                PasswordVerifyResponse.class
        );

        System.out.println("正确密码测试结果: " + JSON.toJSONString(response));
        assert response != null;
        assert response.getSuccess();
        assert "验证成功".equals(response.getMessage());
    }

    @Test
    public void testWrongPassword() {
        // 测试错误密码
        PasswordVerifyRequest request = new PasswordVerifyRequest();
        request.setPassword("wrong_password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PasswordVerifyRequest> entity = new HttpEntity<>(request, headers);

        PasswordVerifyResponse response = restTemplate.postForObject(
                "/api/1.0/auth/verify-password",
                entity,
                PasswordVerifyResponse.class
        );

        System.out.println("错误密码测试结果: " + JSON.toJSONString(response));
        assert response != null;
        assert !response.getSuccess();
        assert response.getMessage().contains("密码错误");
    }

    @Test
    public void testEmptyPassword() {
        // 测试空密码
        PasswordVerifyRequest request = new PasswordVerifyRequest();
        request.setPassword("");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PasswordVerifyRequest> entity = new HttpEntity<>(request, headers);

        PasswordVerifyResponse response = restTemplate.postForObject(
                "/api/1.0/auth/verify-password",
                entity,
                PasswordVerifyResponse.class
        );

        System.out.println("空密码测试结果: " + JSON.toJSONString(response));
        assert response != null;
        assert !response.getSuccess();
        assert "密码不能为空".equals(response.getMessage());
    }
}

