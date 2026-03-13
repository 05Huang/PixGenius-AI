package cn.hx.pix.genius.comfyui.client.pojo;

import lombok.Data;

import java.util.UUID;

@Data
public class ComfyuiTask {

    String id = UUID.randomUUID().toString();
    String wsClientId;
    ComfyuiRequestDto comfyuiRequestDto;
    // comfyui中任务的唯一ID
    String promptId;
    // 当前任务在队列中的分配序号
    long index;
    Long userId;
    int size;// 生成的图片

    public ComfyuiTask(String wsClientId, ComfyuiRequestDto comfyuiRequestDto) {
        this.wsClientId = wsClientId;
        this.comfyuiRequestDto = comfyuiRequestDto;
    }
}
