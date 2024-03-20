package com.bsolz.reactivekafkaconsumer.configuration;

import com.bsolz.reactivekafka.models.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, Location> kafkaReceiverOptions(@Value("${spring.kafka.topic.name}") String topic,
                                                                  KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Location> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties(null));

        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Location> reactiveKafkaConsumerTemplate(ReceiverOptions<String, Location> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<String, Location>(kafkaReceiverOptions);
    }
}
