package com.bsolz.reactivekafkaconsumer.repository;

import com.bsolz.reactivekafkaconsumer.entities.LocationDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LocationRepository extends ReactiveCrudRepository<LocationDetails, Integer> {
    @Query("SELECT * FROM locations WHERE geo_id = :geoId")
    Mono<LocationDetails> findByGeoId(String geoId);
}
