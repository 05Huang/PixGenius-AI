package cn.hx;

import cn.hx.pix.genius.comfyui.client.api.ComfyuiApi;
import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiRequestDto;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;

@SpringBootTest
public class ComfyuiApiTest {

    @Autowired
    ComfyuiApi comfyuiApi;

    @Test
    public void testHistory() throws IOException {
        Call<HashMap> history = comfyuiApi.getHistoryTasks(2);
        Response<HashMap> execute = history.execute();
        System.out.println(JSON.toJSONString(execute.body()));
    }

    @Test
    public void testGetSystemStatus() throws IOException {
        Call<HashMap> history = comfyuiApi.getSystemStats();
        Response<HashMap> execute = history.execute();
        System.out.println(JSON.toJSONString(execute.body()));
    }

    @Test
    public void testPrompt() throws IOException {
        String json = "{\n" +
                "\"2\": {\n" +
                "\"inputs\": {\n" +
                "\"ckpt_name\": \"majicmixRealistic_v7.safetensors\"\n" +
                "},\n" +
                "\"class_type\": \"CheckpointLoaderSimple\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"Load Checkpoint\"\n" +
                "}\n" +
                "},\n" +
                "\"3\": {\n" +
                "\"inputs\": {\n" +
                "\"text\": \"1 girl\",\n" +
                "\"clip\": [\n" +
                "\"2\",\n" +
                "1\n" +
                "]\n" +
                "},\n" +
                "\"class_type\": \"CLIPTextEncode\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"CLIP Text Encode (Prompt)\"\n" +
                "}\n" +
                "},\n" +
                "\"4\": {\n" +
                "\"inputs\": {\n" +
                "\"text\": \"\",\n" +
                "\"clip\": [\n" +
                "\"2\",\n" +
                "1\n" +
                "]\n" +
                "},\n" +
                "\"class_type\": \"CLIPTextEncode\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"CLIP Text Encode (Prompt)\"\n" +
                "}\n" +
                "},\n" +
                "\"5\": {\n" +
                "\"inputs\": {\n" +
                "\"seed\": 622556791667887,\n" +
                "\"steps\": 20,\n" +
                "\"cfg\": 8,\n" +
                "\"sampler_name\": \"euler\",\n" +
                "\"scheduler\": \"normal\",\n" +
                "\"denoise\": 1,\n" +
                "\"model\": [\n" +
                "\"2\",\n" +
                "0\n" +
                "],\n" +
                "\"positive\": [\n" +
                "\"3\",\n" +
                "0\n" +
                "],\n" +
                "\"negative\": [\n" +
                "\"4\",\n" +
                "0\n" +
                "],\n" +
                "\"latent_image\": [\n" +
                "\"6\",\n" +
                "0\n" +
                "]\n" +
                "},\n" +
                "\"class_type\": \"KSampler\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"KSampler\"\n" +
                "}\n" +
                "},\n" +
                "\"6\": {\n" +
                "\"inputs\": {\n" +
                "\"width\": 512,\n" +
                "\"height\": 512,\n" +
                "\"batch_size\": 1\n" +
                "},\n" +
                "\"class_type\": \"EmptyLatentImage\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"Empty Latent Image\"\n" +
                "}\n" +
                "},\n" +
                "\"7\": {\n" +
                "\"inputs\": {\n" +
                "\"samples\": [\n" +
                "\"5\",\n" +
                "0\n" +
                "],\n" +
                "\"vae\": [\n" +
                "\"2\",\n" +
                "2\n" +
                "]\n" +
                "},\n" +
                "\"class_type\": \"VAEDecode\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"VAE Decode\"\n" +
                "}\n" +
                "},\n" +
                "\"8\": {\n" +
                "\"inputs\": {\n" +
                "\"filename_prefix\": \"ComfyUI\",\n" +
                "\"images\": [\n" +
                "\"7\",\n" +
                "0\n" +
                "]\n" +
                "},\n" +
                "\"class_type\": \"SaveImage\",\n" +
                "\"_meta\": {\n" +
                "\"title\": \"Save Image\"\n" +
                "}\n" +
                "}\n" +
                "}";
        ComfyuiRequestDto queueTask = new ComfyuiRequestDto("12345456676",JSON.parseObject(json));
        Call<HashMap> history = comfyuiApi.addQueueTask(queueTask);
        Response<HashMap> execute = history.execute();
        System.out.println(JSON.toJSONString(execute.body()));
    }

}
