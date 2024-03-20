package com.bsolz.reactivekafkaconsumer.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("locations")
public class LocationDetails {

    @Id
    private Integer id;
    private String geoId;
    private String geoType;
    private String name;
    private String status;
}
