package cn.hx.pix.genius.ollama.client.config;

import cn.hx.pix.genius.ollama.client.api.OllamaApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class OllamaConfig {


    @Bean
    public OllamaApi ollamaApi(ObjectMapper objectMapper) throws IOException {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format("http://192.168.100.129:11434"))
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper)) // 支持驼峰模式到下划线的属性格式转换
                .build();
        OllamaApi ollamaApi = retrofit.create(OllamaApi.class);
        return ollamaApi;
    }

}
