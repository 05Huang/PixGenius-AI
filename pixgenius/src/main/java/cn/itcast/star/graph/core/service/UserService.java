package cn.hx.pix.genius.core.service;


import cn.hx.pix.genius.core.dto.UserLoginRequestDto;
import cn.hx.pix.genius.core.dto.UserLoginResponeDto;
import cn.hx.pix.genius.core.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* <p>
* sg_user Service 接口
* </p>
*
* @author luoxu
* @since 2024-10-15 14:45:42
*/
public interface UserService extends IService<User> {

    UserLoginResponeDto loginByPassword(UserLoginRequestDto dto);
}