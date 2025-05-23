package org.example.processorservice.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.TopicBuilder;



@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic smsTopic() {
        return TopicBuilder.name("sms-topic")
                .build();
    }

    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder.name("email-topic")
                .build();
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("whatsapp-topic")
                .build();
    }
}