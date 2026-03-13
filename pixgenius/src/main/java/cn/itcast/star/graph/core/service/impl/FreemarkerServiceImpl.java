package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.comfyui.client.pojo.ComfyuiModel;
import cn.hx.pix.genius.core.service.FreemarkerService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class FreemarkerServiceImpl implements FreemarkerService {

    @Autowired
    Configuration configuration;

    @Override
    public String renderText2Image(ComfyuiModel model) throws Exception {
        Template template = configuration.getTemplate("t2i.ftlh");
        Map<String,Object> data = new HashMap<>();
        data.put("config",model);
        StringWriter out = new StringWriter();
//        FileWriter fw = new FileWriter("d:/1.txt");
        /**
         * 第一个参数是，模版要用到的数据
         * 第二个参数是，模版生成的结果存放的位置
         */
        template.process(data,out);//使用数据去替换模版
        return out.toString();
    }
}
