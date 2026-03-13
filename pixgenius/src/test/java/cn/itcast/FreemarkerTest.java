package cn.hx;

import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiModel;
import cn.hx.pix.genius.core.service.FreemarkerService;
import cn.hx.pix.genius.ollama.client.api.OllamaApi;
import cn.hx.pix.genius.ollama.client.pojo.OllamaChatRequest;
import cn.hx.pix.genius.ollama.client.pojo.OllamaChatRespone;
import cn.hx.pix.genius.ollama.client.pojo.OllamaMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class FreemarkerTest {
    @Autowired
    FreemarkerService freemarkerService;

    @Test
    public void test() throws Exception {
        ComfyuiModel model  = new ComfyuiModel();
        model.setModelName("我是模型名称===================");
        model.setSize(2);
        model.setPropmt("a photo of an astronaut riding a horse on mars");
        model.setReverse("bad legs");
        model.setSamplerName("Euler");
        model.setScheduler("normal");
        model.setSize(1);
        model.setCfg(7);
        model.setWidth(512);
        model.setHeight(512);
        model.setStep(20);
        model.setSeed(0);
        String out = freemarkerService.renderText2Image(model);
        System.out.println(out);
    }
}
