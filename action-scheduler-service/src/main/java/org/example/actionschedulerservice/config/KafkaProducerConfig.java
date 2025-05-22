package org.example.actionschedulerservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.actionschedulerservice.repository.beans.Action;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers ;

    public Map<String , Object> producerConfig()
    {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576); // 1MB
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return configProps;
    }

    public ProducerFactory<String, Action> producerFactory()
    {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean  //way how to send message
    public KafkaTemplate<String, Action> kafkaTemplate( )
    {
        return new KafkaTemplate<String, Action>(producerFactory());
    }
}
