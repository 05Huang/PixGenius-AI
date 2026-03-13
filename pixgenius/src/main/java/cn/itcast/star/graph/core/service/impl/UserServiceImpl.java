package cn.hx.pix.genius.core.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hx.pix.genius.core.dto.UserLoginRequestDto;
import cn.hx.pix.genius.core.dto.UserLoginResponeDto;
import cn.hx.pix.genius.core.exception.CustomException;
import cn.hx.pix.genius.core.mapper.UserMapper;
import cn.hx.pix.genius.core.pojo.User;
import cn.hx.pix.genius.core.service.UserService;
import cn.hx.pix.genius.core.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
* sg_user Service 接口实现
* </p>
*
* @author luoxu
* @since 2024-10-15 14:46:22
*/
@Service
@Transactional
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户登录的业务逻辑
     * @param dto
     * @return
     */
    @Override
    public UserLoginResponeDto loginByPassword(UserLoginRequestDto dto) {
        //  1、参数判断
        if(StrUtil.isBlank(dto.getUsername())||StrUtil.isBlank(dto.getPassword())){
            throw new CustomException("用户名或密码不能为空！");
        }
        //  2、查数据库：通过用户名查询数据库？查手机和用户名
//        LambdaQueryChainWrapper<User> wapper = lambdaQuery().eq(User::getMobile, dto.getUsername())
//                .or()
//                .eq(User::getUsername, dto.getUsername());
//        LambdaQueryWrapper<User> wapper = Wrappers.<User>lambdaQuery().or(a -> a.eq(User::getMobile, "admin").or().eq(User::getUsername, "admin"));
        LambdaQueryWrapper<User> wapper = Wrappers.<User>lambdaQuery().eq(User::getMobile, dto.getUsername())
                .or()
                .eq(User::getUsername, dto.getUsername());

        User user = baseMapper.selectOne(wapper);
        if(user==null){
            throw new CustomException("用户名或密码不正确！");
        }
        //  3、判断输入的密码和数据库中的密码是否一致？   数据库存储的密码是加密（MD5+盐）
        //  3.1、把输入的密码按照加密规则生成
        //  3.2、对比密码
        if(!BCrypt.checkpw(dto.getPassword(),user.getPassword())){
            throw new CustomException("用户名或密码不正确！");
        }
        //  4、生成token并按格式返回
        UserLoginResponeDto userLoginResponeDto = new UserLoginResponeDto();
        userLoginResponeDto.setAvatar(user.getAvatar());
        userLoginResponeDto.setId(user.getId());
        userLoginResponeDto.setName(user.getUsername());
        userLoginResponeDto.setToken(JwtUtils.genToekn(user));
        return userLoginResponeDto;
    }
}