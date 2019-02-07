package com.noovitec.mpb.entity;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	@Column(name = "id")
	private Long id;
	private String name;
	private String supplier;
	private String stockNumber;
	private String vendorStockNumber;
	private String description;
	private String picture;
	private String assumedPrice;
//    @ManyToMany(mappedBy="components")
//    @JoinTable(name="item_component",
//    	uniqueConstraints=@UniqueConstraint(columnNames= {"item_id", "component_id"}),
//    	joinColumns={@JoinColumn(name="component_id", referencedColumnName="id")}, 
//    	inverseJoinColumns={@JoinColumn(name="item_id", referencedColumnName="id")})
//	private Collection<Item> items;
}