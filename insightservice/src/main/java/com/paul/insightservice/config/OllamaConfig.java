package com.paul.insightservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are an expert energy efficiency advisor.
                        
                        Analyze household energy consumption patterns and provide:
                        - concise insights
                        - anomaly detection
                        - practical energy-saving recommendations
                        - comparison with average household usage
                        
                        Keep responses short, practical, and user friendly.
                        """)
                .build();
    }
}