package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Component {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String supplier;
    private String stockNumber;
    private String vendorStockNumber;
    private String description;
    private String picture;
    private String assumedPrice;
}