package cn.hx;

import cn.hutool.crypto.digest.BCrypt;
import cn.hx.pix.genius.core.mapper.UserMapper;
import cn.hx.pix.genius.core.pojo.User;
import cn.hx.pix.genius.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class UserTest {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setAvatar("https://img1.baidu.com/it/u=1090452517,2487311686&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1729098000&t=bde85038085ca68a7e26aef57b0b6b2b");
        user.setUsername("admin1");
        user.setPassword(BCrypt.hashpw("admin"));
        user.setMobile("1521030727");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }
}
