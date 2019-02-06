package com.noovitec.mpb.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String stockNumber;
    private String description;
    private String assumedPrice;
    @OneToMany()
    @JoinTable(name="item_component", 
    	joinColumns={@JoinColumn(name="item_id", referencedColumnName="id")}, 
    	inverseJoinColumns={@JoinColumn(name="component_id", referencedColumnName="id")})
    public Collection<Component> components;
}