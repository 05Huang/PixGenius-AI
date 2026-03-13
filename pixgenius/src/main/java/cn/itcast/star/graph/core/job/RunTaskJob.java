package cn.hx.pix.genius.core.job;

import cn.hutool.core.date.DateTime;
import cn.hx.pix.genius.comfyui.client.api.ComfyuiApi;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiTask;
import cn.hx.pix.genius.core.service.RedisService;
import cn.hx.pix.genius.core.service.UserFundRecordService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import jakarta.annotation.PostConstruct;

@Component
@Log4j2
public class RunTaskJob {

    final static String SPRING_TASK_LOCK_KEY = "SPRING_TASK_LOCK_KEY";
    public final static String TASK_RUN_SEMAPHORE = "TASK_RUN_SEMAPHORE";

    @Autowired
    ComfyuiApi comfyuiApi;
    @Autowired
    RedisService redisService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    UserFundRecordService userFundRecordService;

    /**
     * 初始化信号量，避免未设定许可导致 tryAcquire 一直失败
     */
    @PostConstruct
    public void initSemaphore() {
        RSemaphore semaphore = redissonClient.getSemaphore(TASK_RUN_SEMAPHORE);
        // 仅在首次初始化时设置许可数，已存在时保持原值
        semaphore.trySetPermits(1);
        log.info("初始化任务信号量完成，当前许可={}", semaphore.availablePermits());
    }

    /**
     * 把任务发送给comfyui
     */
    private void sendTaskToComfyui(){
        ComfyuiTask comfyuiTask = redisService.popQueueTask();
        if(comfyuiTask==null){
            log.info("没有待发送的ComfyUI任务");
            return;
        }
        log.info("准备发送任务到ComfyUI, taskId={}, wsClientId={}, queueIndex={}", comfyuiTask.getId(),
                comfyuiTask.getWsClientId(), comfyuiTask.getIndex());
        log.debug("发送ComfyUI请求体: {}", JSON.toJSONString(comfyuiTask.getComfyuiRequestDto()));
        // 发送到comfyui
        Call<HashMap> addQueueTask = comfyuiApi.addQueueTask(comfyuiTask.getComfyuiRequestDto());
        try {
            Response<HashMap> response = addQueueTask.execute();
            if(response.isSuccessful()) {
                HashMap map = response.body();
                String promptId = (String) map.get("prompt_id");
                comfyuiTask.setPromptId(promptId);
                log.info("添加任务到Comfyui成功：promptId={}, taskId={}",comfyuiTask.getPromptId(), comfyuiTask.getId());
                redisService.addStartedTask(promptId,comfyuiTask);
            }else{
                String error = response.errorBody().string();
                log.error("添加任务到Comfyui错误: taskId={}, error={}", comfyuiTask.getId(), error);
                // 归还信号量
                RSemaphore semaphore = redissonClient.getSemaphore(TASK_RUN_SEMAPHORE);
                semaphore.release();
                log.info("任务发送失败释放信号量，当前剩余许可={}", semaphore.availablePermits());
                // 冻结归还
                userFundRecordService.freezeReturn(comfyuiTask.getUserId(),comfyuiTask.getSize());
            }
        } catch (IOException e) {
            log.error("添加任务到ComfyUI异常: taskId={}", comfyuiTask.getId(), e);
        }
    }

    /**
     * 每秒执行一次
     */
    @Scheduled(cron="*/1 * * * * ?")
    public void runTask() {
        // 获取锁
        RLock lock = redissonClient.getLock(SPRING_TASK_LOCK_KEY);
        // 占有锁
        if(lock.tryLock()) {
            try {
                RSemaphore semaphore = redissonClient.getSemaphore(TASK_RUN_SEMAPHORE);
                boolean hasTask = redisService.hasQueueTask();
                if(hasTask) {
                    log.info("定时调度检测，hasQueueTask={}, semaphorePermits={}", hasTask, semaphore.availablePermits());
                    // 占用一个信号量，如果占用成功才发送任务
                    if (semaphore.tryAcquire()) {
                        log.info("检测到待处理任务，信号量占用成功，当前剩余许可={}", semaphore.availablePermits());
                        sendTaskToComfyui();
//              System.out.println("task1" + DateTime.now());
                    } else {
                        log.warn("信号量不足，暂不发送任务，剩余许可={}", semaphore.availablePermits());
                    }
                }
            }finally {
                lock.unlock();// 释放锁
            }
        }
    }

}
