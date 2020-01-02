package com.example.demo.configuration;

import com.example.demo.model.transaction.TransactionEvent;
import com.example.demo.service.EventSenderService;
import com.example.demo.service.EventSenderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

@EnableAsync
@Configuration
public class KafkaProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerConfig.class);

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, TransactionEvent> producerFactory(ObjectMapper objectMapper) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new JsonSerializer(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, TransactionEvent> kafkaTemplate(ObjectMapper objectMapper) {
        return new KafkaTemplate<String, TransactionEvent>(producerFactory(objectMapper));
    }

    @Bean
    public EventSenderService eventSenderService() {
        return new EventSenderServiceImpl();
    }
}
