package com.twb.dadale.config;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {
    private String apiKey; // 智谱 API Key
    private String aliApiKey;   // 阿里百炼 API Key

    @Bean
    public ClientV4 chatGLMClient() {
        return new ClientV4.Builder(apiKey).build();
    }

    @Bean
    public Generation getAliGenerationClient() {
        return new Generation();
    }
}

