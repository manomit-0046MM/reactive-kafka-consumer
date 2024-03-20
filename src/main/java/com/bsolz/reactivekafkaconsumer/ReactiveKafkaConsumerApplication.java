package com.bsolz.reactivekafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReactiveKafkaConsumerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReactiveKafkaConsumerApplication.class, args);
	}

}
