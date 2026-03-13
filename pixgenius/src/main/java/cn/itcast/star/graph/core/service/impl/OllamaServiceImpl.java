package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.service.OllamaService;
import cn.hx.pix.genius.ollama.client.api.OllamaApi;
import cn.hx.pix.genius.ollama.client.pojo.OllamaChatRequest;
import cn.hx.pix.genius.ollama.client.pojo.OllamaChatRespone;
import cn.hx.pix.genius.ollama.client.pojo.OllamaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

@Service
@Slf4j
public class OllamaServiceImpl implements OllamaService {
    @Autowired
    OllamaApi ollamaApi;

    @Override
    public String translate(String propmt) {
        try {
            log.info("开始翻译提示词: {}", propmt);
            OllamaMessage message = new OllamaMessage();
            message.setRole("user");
            message.setContent("帮我把以下内容翻译成英文：" + propmt);
            OllamaChatRequest body = new OllamaChatRequest();
            body.setModel("qwen2:0.5b");
            body.setMessages(List.of(message));
            Call<OllamaChatRespone> chat = ollamaApi.chat(body);
            Response<OllamaChatRespone> result = chat.execute();
            OllamaChatRespone ollamaChatRespone = result.body();
            String content = ollamaChatRespone.getMessage().getContent();
            log.info("翻译完成, 原文='{}', 英文='{}'", propmt, content);
            return content;
        }catch (Exception e){
            log.error("翻译提示词失败, fallback使用原文: {}", propmt, e);
        }
        return propmt;
    }


}
