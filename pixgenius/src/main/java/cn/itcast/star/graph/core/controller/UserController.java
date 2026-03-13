package cn.hx.pix.genius.core.controller;

import cn.hx.pix.genius.core.dto.UserLoginRequestDto;
import cn.hx.pix.genius.core.dto.UserLoginResponeDto;
import cn.hx.pix.genius.core.dto.common.Result;
import cn.hx.pix.genius.core.pojo.SgUserFund;
import cn.hx.pix.genius.core.service.UserFundRecordService;
import cn.hx.pix.genius.core.service.UserService;
import cn.hx.pix.genius.core.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* <p>
* sg_user 控制器实现
* </p>
*
* @author luoxu
* @since 2024-10-15 14:47:14
*/
@RestController
@RequestMapping("/api/1.0/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserFundRecordService userFundRecordService;

    @PostMapping("/login")
    public Result<UserLoginResponeDto> login(@RequestBody UserLoginRequestDto dto){
        UserLoginResponeDto userLoginResponeDto = userService.loginByPassword(dto);
        return Result.ok(userLoginResponeDto);
    }

    @GetMapping("/fund")
    public Result<SgUserFund> getFund(){
        return Result.ok(userFundRecordService.getUserFund(UserUtils.getUser().getId()));
    }
}
