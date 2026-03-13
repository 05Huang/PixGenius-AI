package cn.hx.pix.genius.core.controller;

import cn.hx.pix.genius.core.dto.Text2ImageReqDto;
import cn.hx.pix.genius.core.dto.Text2ImageResponeDto;
import cn.hx.pix.genius.core.dto.T2iCapabilityDto;
import cn.hx.pix.genius.core.dto.UserLoginRequestDto;
import cn.hx.pix.genius.core.dto.UserLoginResponeDto;
import cn.hx.pix.genius.core.dto.common.Result;
import cn.hx.pix.genius.core.config.T2iProperties;
import cn.hx.pix.genius.core.pojo.UserResult;
import cn.hx.pix.genius.core.service.UserResultService;
import cn.hx.pix.genius.core.utils.UserUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hx.pix.genius.core.service.Text2ImageService;
import cn.hx.pix.genius.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 文生图接口
* </p>
*
* @author luoxu
* @since 2024-10-15 14:47:14
*/
@RestController
@RequestMapping("/api/authed/1.0/t2i")
public class Text2ImageController {

    @Autowired
    private Text2ImageService text2ImageService;
    @Autowired
    private T2iProperties t2iProperties;
    @Autowired
    private UserResultService userResultService;

    @PostMapping("/propmt")
    public Result<Text2ImageResponeDto> propmt(@RequestBody Text2ImageReqDto text2ImageReqDto) throws Exception {
        Text2ImageResponeDto text2ImageResponeDto = text2ImageService.textToImage(text2ImageReqDto);
        return Result.ok(text2ImageResponeDto);
    }

    @PostMapping("/canel")
    public Result<Boolean> cancel(@RequestBody Text2ImageResponeDto req){
        boolean ok = text2ImageService.cancelTask(req.getPid());
        return ok ? Result.ok(true) : Result.error("任务取消失败或已被取走");
    }

    @PostMapping("/proprity")
    public Result<Long> priority(@RequestBody Text2ImageResponeDto req){
        long newIndex = text2ImageService.priorityTask(req.getPid(),10);
        return Result.ok(newIndex);
    }

    /**
     * 返回当前环境的生成能力（是否开启本地SD）
     */
    @GetMapping("/capability")
    public Result<T2iCapabilityDto> capability(){
        T2iCapabilityDto dto = new T2iCapabilityDto();
        dto.setSdEnabled(t2iProperties.isSdEnabled());
        return Result.ok(dto);
    }

    /**
     * 当前用户生成结果分页
     */
    @PostMapping("/list")
    public Result<Page<UserResult>> list(@RequestBody cn.hx.pix.genius.core.dto.common.PageRequestDto pageRequest){
        Long userId = UserUtils.getUser().getId();
        Page<UserResult> page = userResultService.pageByUser(userId, pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.ok(page);
    }

}
