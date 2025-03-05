package com.twb.dadale.manager;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.twb.dadale.config.AiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class AliyunAiManager {
    @Autowired
    private Generation aliGenerationClient;

    @Autowired
    private AiConfig aiConfig;

    /**
     * 直接同步请求阿里百炼
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, false, temperature);
    }

    /**
     * 适配 ChatGLM 的 `doRequest` 结构
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<Message> chatMessageList = new ArrayList<>();
        chatMessageList.add(Message.builder().role(Role.SYSTEM.getValue()).content(systemMessage).build());
        chatMessageList.add(Message.builder().role(Role.USER.getValue()).content(userMessage).build());

        return doRequest(chatMessageList, stream, temperature);
    }

    /**
     * 适配 ChatGLM 的 `doRequest(List<ChatMessage>)` 方法
     */
    public String doRequest(List<Message> messages, Boolean stream, Float temperature) {
        GenerationParam param = GenerationParam.builder()
                .apiKey(aiConfig.getAliApiKey()) // 读取阿里百炼 API Key
                .model("qwen-plus")
                .messages(messages)
                .temperature(temperature)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        try {
            GenerationResult result = aliGenerationClient.call(param);
            return result.getOutput().getChoices().get(0).getMessage().getContent();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            e.printStackTrace();
            throw new RuntimeException("调用阿里百炼 API 失败", e);
        }
    }
}
