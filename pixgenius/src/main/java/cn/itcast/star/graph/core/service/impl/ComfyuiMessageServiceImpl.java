package cn.hx.pix.genius.core.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiTask;
import cn.hx.pix.genius.comfyui.client.pojo.MessageBase;
import cn.hx.pix.genius.core.job.RunTaskJob;
import cn.hx.pix.genius.core.pojo.UserResult;
import cn.hx.pix.genius.core.service.*;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComfyuiMessageServiceImpl implements ComfyuiMessageService {

    @Autowired
    WsNoticeService wsNoticeService;
    @Autowired
    RedisService redisService;
    @Autowired
    UserResultService userResultService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    UserFundRecordService userFundRecordService;


    @Override
    public void handleMessage(MessageBase messageBase) {
        log.info("处理ComfyUI消息, type={}, dataKeys={}", messageBase.getType(),
                messageBase.getData()!=null? messageBase.getData().keySet():null);
        if("progress".equals(messageBase.getType())){
            // 进度消息
            handleProgressMessage(messageBase);
        }else if("executed".equals(messageBase.getType())){
            // 生图结果消息
            handleExecutedMessage(messageBase);
        }else if("execution_error".equals(messageBase.getType())){
            // 失败消息
            handleExecutionErrorMessage(messageBase);
        }else if("status".equals(messageBase.getType())){
            handleStatusMessage(messageBase);
        }
    }

    private void handleStatusMessage(MessageBase messageBase) {
        HashMap<String, Object> data = messageBase.getData();
        HashMap<String, Object> status = (HashMap<String, Object>) data.get("status");
        HashMap<String, Object> execInfo = (HashMap<String, Object>) status.get("exec_info");
        int queueRemaining = Integer.valueOf(execInfo.get("queue_remaining")+"");
        log.info("收到ComfyUI状态消息, queueRemaining={}", queueRemaining);
        if(queueRemaining==0){
            // comfyui中没有要执行的任务，则我开始发送任务给comfyui
            RSemaphore semaphore = redissonClient.getSemaphore(RunTaskJob.TASK_RUN_SEMAPHORE);
            semaphore.release();//归还信号量
            log.info("{} 释放许可数量 {}", DateTime.now(), semaphore.availablePermits());
        }
    }

    /**
     * 推送失败消息
     * @param messageBase
     */
    private void handleExecutionErrorMessage(MessageBase messageBase) {
        HashMap<String, Object> data = messageBase.getData();
        String promptId = (String) data.get("prompt_id");
        log.warn("ComfyUI执行失败, promptId={}, data={}", promptId, data);
        ComfyuiTask task = redisService.getStartedTask(promptId);
        data.put("type","execution_error");
        if(task==null){
            log.warn("未找到正在执行的任务缓存, promptId={}", promptId);
            return;
        }
        // 积分退还
        userFundRecordService.directDeduction(task.getUserId(), -task.getSize());
        wsNoticeService.sendToUser(task.getWsClientId(), JSON.toJSONString(data));
        log.info("失败消息已推送给客户端, wsClientId={}", task.getWsClientId());
    }

    /**
     * 推送生图结果
     * @param messageBase
     */
    private void handleExecutedMessage(MessageBase messageBase) {
        // 转化格式
        HashMap<String, Object> data = messageBase.getData();
        HashMap<String, Object> output = (HashMap<String, Object>) data.get("output");
        List<HashMap<String, Object>> images = (List<HashMap<String, Object>>) output.get("images");
        List<String> urls = images.stream().map((image) -> String.format("http://192.168.100.129:8188/view?filename=%s&type=%s&subfolder=", image.get("filename"), image.get("type")))
                .collect(Collectors.toList());
        HashMap<String,Object> temp = new HashMap<>();
        temp.put("type","imageResult");
        temp.put("urls",urls);
        String promptId = (String) data.get("prompt_id");
        ComfyuiTask task = redisService.getStartedTask(promptId);
        if(task==null){
            log.warn("生成结果未找到任务缓存, promptId={}", promptId);
            return;
        }
        // userResultService.saveList(urls,task.getUserId());
        // 改为只保存结果，不扣除积分（因为任务提交时已扣除）
        List<UserResult> userResults = urls.stream().map((url) -> {
            UserResult userResult = new UserResult();
            userResult.setUserId(task.getUserId());
            userResult.setUrl(url);
            userResult.setCollect(0);
            return userResult;
        }).collect(Collectors.toList());
        userResultService.saveBatch(userResults);
        
        wsNoticeService.sendToUser(task.getWsClientId(),JSON.toJSONString(temp));
        log.info("生成结果已推送, promptId={}, urls={}, wsClientId={}", promptId, urls, task.getWsClientId());
    }

    private void handleProgressMessage(MessageBase messageBase) {
        HashMap<String, Object> data = messageBase.getData();
        String promptId = (String) data.get("prompt_id");
        ComfyuiTask task = redisService.getStartedTask(promptId);
        data.put("type","progress");
        if(task==null){
            log.warn("进度消息未找到任务缓存, promptId={}", promptId);
            return;
        }
        log.debug("转发进度消息, promptId={}, progress={}", promptId, data);
        wsNoticeService.sendToUser(task.getWsClientId(), JSON.toJSONString(data));
    }

}
