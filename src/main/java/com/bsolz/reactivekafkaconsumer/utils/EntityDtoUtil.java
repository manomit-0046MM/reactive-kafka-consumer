package com.bsolz.reactivekafkaconsumer.utils;

import com.bsolz.reactivekafkaconsumer.entities.LocationDetails;
import com.bsolz.reactivekafka.models.Location;

public class EntityDtoUtil {

    public static LocationDetails toEntity(Location location) {
        LocationDetails location1 = new LocationDetails();
        location1.setGeoId(location.getGeoId());
        location1.setGeoType(location.getGeoType());
        location1.setName(location.getName());
        location1.setStatus(location.getStatus());
        return location1;
    }
}
