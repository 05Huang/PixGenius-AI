package cn.hx.pix.genius.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiModel;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiRequestDto;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiTask;
import cn.hx.pix.genius.comfyui.client.api.ComfyuiApi;
import cn.hx.pix.genius.core.config.T2iProperties;
import cn.hx.pix.genius.core.common.Constants;
import cn.hx.pix.genius.core.dto.Text2ImageReqDto;
import cn.hx.pix.genius.core.dto.Text2ImageResponeDto;
import cn.hx.pix.genius.core.exception.CustomException;
import cn.hx.pix.genius.core.pojo.UserResult;
import cn.hx.pix.genius.core.service.*;
import cn.hx.pix.genius.core.utils.UserUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Text2ImageServiceImpl implements Text2ImageService {

    @Autowired
    OllamaService ollamaService;
    @Autowired
    FreemarkerService freemarkerService;
    @Autowired
    RedisService redisService;
    @Autowired
    UserFundRecordService userFundRecordService;
    @Autowired
    T2iProperties t2iProperties;
    @Autowired
    ComfyuiApi comfyuiApi;
    @Autowired
    AliImageService aliImageService;
    @Autowired
    UserResultService userResultService;
    @Autowired
    WsNoticeService wsNoticeService;
    @Autowired
    AliyunCaptchaService captchaService;

    /**
     * 把请求参数封装成comfyui的请求对象
     * @param text2ImageReqDto
     * @return
     * @throws Exception
     */
    public ComfyuiTask getComfyuiTask(Text2ImageReqDto text2ImageReqDto) throws Exception {
        log.info("开始封装ComfyUI任务, wsClientId={}, model={}, size={}, seed={}, step={}",
                text2ImageReqDto.getClientId(), text2ImageReqDto.modelName(),
                text2ImageReqDto.getSize(), text2ImageReqDto.getSeed(), text2ImageReqDto.getStep());
        ComfyuiModel comfyuiModel = new ComfyuiModel();
        BeanUtil.copyProperties(text2ImageReqDto,comfyuiModel,true);
        comfyuiModel.setModelName(text2ImageReqDto.modelName());
        comfyuiModel.setSamplerName(text2ImageReqDto.samplerName());
        comfyuiModel.setScheduler(text2ImageReqDto.scheduler());
        comfyuiModel.setWidth(text2ImageReqDto.width());
        comfyuiModel.setHeight(text2ImageReqDto.height());

        // 转换提示词
        String translatedPrompt = ollamaService.translate(text2ImageReqDto.getPropmt());
        String translatedNegative = ollamaService.translate(text2ImageReqDto.getReverse());
        log.info("翻译结果, wsClientId={}, positive={}, negative={}", text2ImageReqDto.getClientId(),
                translatedPrompt, translatedNegative);
        comfyuiModel.setPropmt("(8k, best quality, masterpiece),(high detailed skin),"+translatedPrompt);
        // 负向词
        comfyuiModel.setReverse(translatedNegative+",bad face,naked,bad finger,bad arm,bad leg,bad eye");

        // 生成comfyui的请求对象
        String propmt = freemarkerService.renderText2Image(comfyuiModel);
        ComfyuiRequestDto requestDto = new ComfyuiRequestDto(Constants.COMFYUI_CLIENT_ID, JSON.parseObject(propmt));
        log.debug("生成ComfyUI请求体, wsClientId={}, body={}", text2ImageReqDto.getClientId(), propmt);

        // 最后构造ComfuiTask对象
        ComfyuiTask comfyuiTask = new ComfyuiTask(text2ImageReqDto.getClientId(), requestDto);
        // 存储用户ID
        comfyuiTask.setUserId(UserUtils.getUser().getId());
        comfyuiTask.setSize(text2ImageReqDto.getSize());
        log.info("封装ComfyUI任务完成, wsClientId={}, taskId={}, queueIndex={}", text2ImageReqDto.getClientId(),
                comfyuiTask.getId(), comfyuiTask.getIndex());
        return comfyuiTask;
    }

    /**
     * 文生图接口实现
     * @param text2ImageReqDto
     * @return
     * @throws Exception
     */
    @Override
    public Text2ImageResponeDto textToImage(Text2ImageReqDto text2ImageReqDto) throws Exception {
        // 1. 人机验证（阿里云验证码2.0）
        captchaService.verify(text2ImageReqDto.getCaptchaToken());

        // 2. 基本参数校验
        if(text2ImageReqDto.getSize()<1){
            throw new CustomException("请求参数错误！");
        }
        String provider = text2ImageReqDto.getProvider();
        // 根据环境决定默认生成引擎：本地环境默认 SD，云环境（未启用 SD）默认通义千问
        if(provider==null || provider.isEmpty()){
            provider = t2iProperties.isSdEnabled() ? "sd" : "ali";
        }
        // 云环境关闭 SD，则不允许选择 SD，引导用户使用通义千问
        if("sd".equalsIgnoreCase(provider) && !t2iProperties.isSdEnabled()){
            throw new CustomException("当前环境未启用本地 Stable Diffusion，请选择通义千问-Image-Plus");
        }

        // === 通义千问 Image-Plus 分支：直接调用百炼平台，不走 ComfyUI/Redis 队列 ===
        if("ali".equalsIgnoreCase(provider)){
            log.info("收到通义千问 Image-Plus 文生图请求, wsClientId={}, size={}", text2ImageReqDto.getClientId(),
                    text2ImageReqDto.getSize());
            Long userId = UserUtils.getUser().getId();
            
            // 1. 先扣除积分 (支持失败回滚/退款)
            userFundRecordService.directDeduction(userId, text2ImageReqDto.getSize() * 2);
            
            try{
                // 直接使用原始中文提示词与负向词，并尽可能利用前端的参数配置（比例/张数/步数/强度/种子等）
                List<String> urls = aliImageService.generateImage(
                        text2ImageReqDto.getPropmt(),
                        text2ImageReqDto.getReverse(),
                        text2ImageReqDto.width(),
                        text2ImageReqDto.height(),
                        text2ImageReqDto.getSize(),
                        text2ImageReqDto.getStep(),
                        text2ImageReqDto.getCfg(),
                        text2ImageReqDto.getSeed(),
                        text2ImageReqDto.getSampler(),
                        text2ImageReqDto.getModel()
                );
                
                // 2. 保存结果 (不再调用 saveList 以免重复扣除 freeze 积分)
                List<UserResult> userResults = urls.stream().map((url) -> {
                    UserResult userResult = new UserResult();
                    userResult.setUserId(userId);
                    userResult.setUrl(url);
                    userResult.setCollect(0);
                    return userResult;
                }).collect(Collectors.toList());
                userResultService.saveBatch(userResults);

                // 通过 WebSocket 主动推送给前端（与 ComfyUI 返回格式保持一致）
                HashMap<String,Object> temp = new HashMap<>();
                temp.put("type","imageResult");
                temp.put("urls",urls);
                wsNoticeService.sendToUser(text2ImageReqDto.getClientId(), JSON.toJSONString(temp));
                // 调试期间，顺便广播一份，避免用户目的地路由异常时丢消息
                wsNoticeService.sendToAll(JSON.toJSONString(temp));
                // 直接返回，无队列信息
                Text2ImageResponeDto resp = new Text2ImageResponeDto();
                resp.setPid(null);
                resp.setQueueIndex(0L);
                return resp;
            }catch (Exception e){
                log.error("调用通义千问 Image-Plus 生成图片失败", e);
                // 失败退款
                userFundRecordService.directDeduction(userId, -text2ImageReqDto.getSize() * 2);
                throw new CustomException("通义千问 Image-Plus 生成失败：" + e.getMessage());
        }
        }

        // === 本地 SD/ComfyUI 分支：保持原有逻辑 ===
        log.info("收到文生图请求, wsClientId={}, size={}, provider={}", text2ImageReqDto.getClientId(),
                text2ImageReqDto.getSize(), provider);
        // 改为直接扣除积分，不再使用冻结逻辑
        userFundRecordService.directDeduction(UserUtils.getUser().getId(),text2ImageReqDto.getSize() * 2);
        // 封装数据：
        ComfyuiTask comfyuiTask = getComfyuiTask(text2ImageReqDto);
        // 保存到redis
        comfyuiTask = redisService.addQueueTask(comfyuiTask);
        long queueSize = redisService.queueSize();
        log.info("任务入队完成, wsClientId={}, taskId={}, queueIndex={}, queueSize={}", text2ImageReqDto.getClientId(),
                comfyuiTask.getId(), comfyuiTask.getIndex(), queueSize);
        // 返回数据
        Text2ImageResponeDto text2ImageResponeDto = new Text2ImageResponeDto();
        text2ImageResponeDto.setPid(comfyuiTask.getId());
        text2ImageResponeDto.setQueueIndex(queueSize);
        return text2ImageResponeDto;
    }

    @Override
    public boolean cancelTask(String taskId) {
        if(taskId == null || taskId.isEmpty()){
            // 如果 taskId 为空（例如通义千问模式），则直接视为取消成功（不进行后端操作，前端停止等待即可）
            // 但考虑到积分问题，如果是通义千问模式且已扣分，这里无法退款，因为任务仍在进行。
            // 此时返回 false 提示无法取消，或者 true 但不退款。
            // 根据前端逻辑，这里抛出异常会被捕获显示。
            throw new CustomException("当前任务不支持取消（或任务ID为空）");
        }
        // 1) 优先取消等待队列
        ComfyuiTask queued = redisService.cancelQueueTask(taskId);
        if(queued != null){
            // 积分退还
            userFundRecordService.directDeduction(queued.getUserId(), -queued.getSize() * 2);
            log.info("取消等待中的任务成功, taskId={}", taskId);
            // 即便只是等待中，也尝试打断当前ComfyUI执行，以尽快停止本轮推理
            try{
                comfyuiApi.interruptTask().execute();
                log.info("已调用ComfyUI interrupt 终止当前执行（队列任务被取消）");
            }catch (Exception e){
                log.error("调用ComfyUI interrupt 失败(队列任务被取消), taskId={}", taskId, e);
            }
            return true;
        }
        // 2) 取消正在执行的任务
        ComfyuiTask running = redisService.getStartedTaskByTaskId(taskId);
        if(running != null){
            try{
                comfyuiApi.interruptTask().execute();
                log.info("已调用ComfyUI interrupt 终止正在执行的任务, taskId={}, promptId={}", running.getId(), running.getPromptId());
            }catch (Exception e){
                log.error("调用ComfyUI interrupt 失败, taskId={}", taskId, e);
                throw new CustomException("取消失败，ComfyUI 终止任务异常");
            }
            redisService.removeStartedTask(running.getPromptId(), running.getId());
            // 积分退还
            userFundRecordService.directDeduction(running.getUserId(), -running.getSize() * 2);
            log.info("取消正在执行的任务成功, taskId={}, promptId={}", running.getId(), running.getPromptId());
            return true;
        }
        throw new CustomException("取消失败，任务不存在或已完成");
    }

    @Override
    public long priorityTask(String taskId, long step) {
        if(taskId == null || taskId.isEmpty()){
            throw new CustomException("当前任务不支持插队（或任务ID为空）");
        }
        boolean ok = redisService.priorityQueueTask(taskId, step);
        if(!ok){
            throw new CustomException("插队失败，任务已被取走或不存在");
        }
        // 返回最新队列大小作为序号显示
        return redisService.queueSize();
    }
}
