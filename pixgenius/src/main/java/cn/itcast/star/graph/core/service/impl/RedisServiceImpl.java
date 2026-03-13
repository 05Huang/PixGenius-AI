package cn.hx.pix.genius.core.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiTask;
import cn.hx.pix.genius.core.service.RedisService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final static String TASK_KEY_PREFIX = "task_";
    private final static String DISTRIBUTED_ID_KEY = "DISTRIBUTED_ID";
    private final static String DISTRIBUTED_QUEUE_KEY = "DISTRIBUTED_QUEUE";
    private final static String RUN_TASK_KEY = "run_task_";
    private final static String RUN_TASK_ID_KEY = "run_task_id_";

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public ComfyuiTask addQueueTask(ComfyuiTask task) {
        // 获取一个分布式自增主键ID
        Long idx = stringRedisTemplate.opsForValue().increment(DISTRIBUTED_ID_KEY);
        task.setIndex(idx);

        // 把任务存入zset集合
        stringRedisTemplate.opsForZSet().add(DISTRIBUTED_QUEUE_KEY,task.getId(),idx);
        // 把整个任务通过string方式存储到redis
        stringRedisTemplate.opsForValue().set(TASK_KEY_PREFIX+task.getId(), JSON.toJSONString(task));
        Long size = stringRedisTemplate.opsForZSet().size(DISTRIBUTED_QUEUE_KEY);
        log.info("任务入队到Redis, taskId={}, queueIndex={}, wsClientId={}, queueSizeNow={}", task.getId(), idx, task.getWsClientId(), size);
        return task;
    }

    @Override
    public ComfyuiTask popQueueTask() {
        Long size = stringRedisTemplate.opsForZSet().size(DISTRIBUTED_QUEUE_KEY);
        if(size>0) {
            ZSetOperations.TypedTuple<String> task = stringRedisTemplate.opsForZSet().popMin(DISTRIBUTED_QUEUE_KEY);
            if (task != null && task.getValue() != null) {
                String taskId = task.getValue();
                String json = stringRedisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
                ComfyuiTask comfyuiTask = JSON.parseObject(json, ComfyuiTask.class);
                Long newSize = stringRedisTemplate.opsForZSet().size(DISTRIBUTED_QUEUE_KEY);
                log.info("从Redis队列弹出任务, taskId={}, queueIndex={}, wsClientId={}, queueSizeAfterPop={}", taskId, task.getScore(), comfyuiTask!=null?comfyuiTask.getWsClientId():null, newSize);
                return comfyuiTask;
            }
        }
        log.info("弹出任务时队列为空");
        return null;
    }

    @Override
    public void addStartedTask(String promptId, ComfyuiTask task) {
        stringRedisTemplate.opsForValue().set(RUN_TASK_KEY+promptId,JSON.toJSONString(task), Duration.ofMinutes(10));
        stringRedisTemplate.opsForValue().set(RUN_TASK_ID_KEY+task.getId(), promptId, Duration.ofMinutes(10));
        log.info("缓存正在执行的任务, promptId={}, taskId={}", promptId, task.getId());
    }

    @Override
    public ComfyuiTask getStartedTask(String promptId) {
        String json = stringRedisTemplate.opsForValue().get(RUN_TASK_KEY + promptId);
        if(StrUtil.isNotEmpty(json)){
            return JSON.parseObject(json,ComfyuiTask.class);
        }
        log.warn("未找到正在执行的任务缓存, promptId={}", promptId);
        return null;
    }

    @Override
    public ComfyuiTask getStartedTaskByTaskId(String taskId) {
        String promptId = stringRedisTemplate.opsForValue().get(RUN_TASK_ID_KEY + taskId);
        if(StrUtil.isEmpty(promptId)){
            return null;
        }
        return getStartedTask(promptId);
    }

    @Override
    public void removeStartedTask(String promptId, String taskId) {
        if(StrUtil.isNotEmpty(promptId)){
            stringRedisTemplate.delete(RUN_TASK_KEY + promptId);
        }
        if(StrUtil.isNotEmpty(taskId)){
            stringRedisTemplate.delete(RUN_TASK_ID_KEY + taskId);
        }
        log.info("移除正在执行的任务缓存, promptId={}, taskId={}", promptId, taskId);
    }

    @Override
    public boolean hasQueueTask() {
        Long size = stringRedisTemplate.opsForZSet().size(DISTRIBUTED_QUEUE_KEY);
        return size>0;
    }

    @Override
    public ComfyuiTask cancelQueueTask(String taskId) {
        String json = stringRedisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
        Long removed = stringRedisTemplate.opsForZSet().remove(DISTRIBUTED_QUEUE_KEY, taskId);
        stringRedisTemplate.delete(TASK_KEY_PREFIX + taskId);
        log.info("取消队列任务, taskId={}, removed={}", taskId, removed);
        if(removed != null && removed > 0 && StrUtil.isNotEmpty(json)){
            return JSON.parseObject(json, ComfyuiTask.class);
        }
        return null;
    }

    @Override
    public boolean priorityQueueTask(String taskId, long step) {
        Double score = stringRedisTemplate.opsForZSet().score(DISTRIBUTED_QUEUE_KEY, taskId);
        if(score == null){
            log.warn("插队失败，未找到任务, taskId={}", taskId);
            return false;
        }
        double newScore = Math.max(1, score - step);
        stringRedisTemplate.opsForZSet().add(DISTRIBUTED_QUEUE_KEY, taskId, newScore);
        String json = stringRedisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
        if(StrUtil.isNotEmpty(json)){
            ComfyuiTask task = JSON.parseObject(json, ComfyuiTask.class);
            task.setIndex((long)newScore);
            stringRedisTemplate.opsForValue().set(TASK_KEY_PREFIX+taskId, JSON.toJSONString(task));
        }
        log.info("插队成功, taskId={}, oldScore={}, newScore={}", taskId, score, newScore);
        return true;
    }

    @Override
    public long queueSize() {
        Long size = stringRedisTemplate.opsForZSet().size(DISTRIBUTED_QUEUE_KEY);
        return size == null ? 0 : size;
    }
}
