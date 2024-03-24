package com.bsolz.reactivekafkaconsumer;

import com.bsolz.reactivekafkaconsumer.entities.LocationDetails;
import com.bsolz.reactivekafkaconsumer.repository.LocationRepository;
import com.bsolz.reactivekafkaconsumer.service.ConsumerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {

    @Mock
    private LocationRepository repository;

    @InjectMocks
    private ConsumerService consumerService;

    private LocationDetails locationDetails;

    @BeforeEach
    void setUp() {
        locationDetails = new LocationDetails();
        locationDetails.setGeoId("ABC123");
        locationDetails.setName("Machu Picchu");
        locationDetails.setStatus("Active");
        locationDetails.setId(1);
        locationDetails.setGeoType("Incan Citadel");
        

    }

    @Test
    void testConsumeLocation() {
        Mono<LocationDetails> detailsMono = Mono.just(locationDetails);
        ReflectionTestUtils.invokeMethod(consumerService, "createOrUpdateLocation", locationDetails, locationDetails);
        lenient().when(repository.save(locationDetails)).thenReturn(detailsMono);

        StepVerifier.create(repository.save(locationDetails))
                .assertNext(Assertions::assertNotNull).verifyComplete();

    }
}
