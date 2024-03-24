package com.bsolz.reactivekafkaconsumer.service;

import com.bsolz.reactivekafkaconsumer.entities.LocationDetails;
import com.bsolz.reactivekafka.models.Location;
import com.bsolz.reactivekafkaconsumer.repository.LocationRepository;
import com.bsolz.reactivekafkaconsumer.utils.EntityDtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    private final ReactiveKafkaConsumerTemplate<String, Location> locationConsumerTemplate;

    private final LocationRepository repository;

    public ConsumerService(
            final ReactiveKafkaConsumerTemplate<String, Location> locationConsumerTemplate,
            final LocationRepository repository
    ) {
        this.locationConsumerTemplate = locationConsumerTemplate;
        this.repository = repository;
    }

    @Scheduled(cron = "0 2 1 * * ?")
    public void consumeLocation() {
        locationConsumerTemplate
                .receive()
                .doOnNext(record -> LOGGER.debug("Received event: key {}", record.key()))
                .flatMap(this::storeLocation)
                .doOnError(ex -> LOGGER.error("Error receiving event, will retry {}", ex.getMessage()))
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(10)))
                .onErrorResume(ex -> Mono.empty())
                .subscribe(record -> record.receiverOffset().acknowledge());
    }

    private Mono<ReceiverRecord<String, Location>> storeLocation(ReceiverRecord<String, Location> locationReceiverRecord) {
        Location location = locationReceiverRecord.value();
        if (location.getGeoType().isEmpty() || location.getName().isEmpty() || !location.getStatus().equalsIgnoreCase("Active")) {
            LOGGER.warn("Validation failed. Please check GeoType {}, Name {} and Status {}", location.getGeoType(), location.getName(), location.getStatus());
            return Mono.just(locationReceiverRecord);
        } else {
            LocationDetails locationDetails = EntityDtoUtil.toEntity(location);
            return this.repository
                    .findByGeoId(location.getGeoId())
                    .flatMap(itemFromDb -> createOrUpdateLocation(itemFromDb, locationDetails))
                    .defaultIfEmpty(locationDetails)
                    .flatMap(this.repository::save)
                    .doOnError(ex -> LOGGER.warn("Error on database operation {}", ex.getMessage(), ex))
                    .retryWhen(Retry.max(3).transientErrors(true))
                    .onErrorResume(ex -> Mono.empty())
                    .then(Mono.just(locationReceiverRecord));
        }
    }

    private Mono<LocationDetails> createOrUpdateLocation(LocationDetails itemFromDb, LocationDetails locationDetails) {
        LOGGER.info("Update will start");
        itemFromDb.setStatus(locationDetails.getStatus());
        itemFromDb.setName(locationDetails.getName());
        itemFromDb.setGeoType(locationDetails.getGeoType());
        itemFromDb.setGeoId(locationDetails.getGeoId());
        return Mono.just(itemFromDb);
    }

}
