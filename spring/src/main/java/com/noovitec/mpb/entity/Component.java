package com.noovitec.mpb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

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
    @Column(name="item_id")
    private Long itemId;
    @ManyToOne
    @JoinTable(name = "item_component",
    	joinColumns = {@JoinColumn(name = "item_id", insertable = false, updatable = false, referencedColumnName = "id")},
    	inverseJoinColumns = {@JoinColumn(name = "component_id", insertable = false, updatable = false, referencedColumnName = "id")})
    private Item item;
}