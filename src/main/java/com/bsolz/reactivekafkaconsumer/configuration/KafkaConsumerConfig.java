package com.bsolz.reactivekafkaconsumer.configuration;

import com.bsolz.reactivekafka.models.Location;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Bean
    public ReceiverOptions<String, Location> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Location> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties(null));

        return basicReceiverOptions.subscription(Collections.singletonList(topicName));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Location> reactiveKafkaConsumerTemplate(ReceiverOptions<String, Location> kafkaReceiverOptions) {
        var consumerProperties = kafkaReceiverOptions.consumerProperties();
        var topicExists = doesTopicExists(topicName, consumerProperties);
        if (!topicExists)
            throw new IllegalArgumentException("Topic does not exist: " + topicName);
        return new ReactiveKafkaConsumerTemplate<String, Location>(kafkaReceiverOptions);
    }

    private static boolean doesTopicExists(String topicName, Map<String, Object> properties) {

        try (var admin = AdminClient.create(properties)) {
            return admin.listTopics().names().get().stream().anyMatch(
                    topic -> topic.equalsIgnoreCase(topicName));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
