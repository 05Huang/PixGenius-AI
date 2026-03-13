package cn.hx.pix.genius.core.service;

import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiTask;

public interface RedisService {

    // 添加一个任务
    public ComfyuiTask addQueueTask(ComfyuiTask task);

    // 从队列zset中取出一个任务
    public ComfyuiTask popQueueTask();

    // 添加开始的任务
    void addStartedTask(String promptId, ComfyuiTask task);

    // 通过promptId获取任务
    ComfyuiTask getStartedTask(String promptId);

    // 通过taskId获取正在执行的任务
    ComfyuiTask getStartedTaskByTaskId(String taskId);

    // 移除正在执行的任务
    void removeStartedTask(String promptId, String taskId);

    // 判断队列中是否有任务
    public boolean hasQueueTask();

    // 取消等待中的任务（根据taskId）
    ComfyuiTask cancelQueueTask(String taskId);

    // 队列插队：将任务提前 step 个名次
    boolean priorityQueueTask(String taskId, long step);

    // 当前等待队列大小
    long queueSize();
}
