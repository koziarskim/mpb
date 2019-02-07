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
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	@Transient
	private String[] componentIds;
	@JsonIgnoreProperties("items")
    @ManyToMany(cascade=CascadeType.MERGE)
    @JoinTable(name="item_component",
    	uniqueConstraints=@UniqueConstraint(columnNames= {"item_id", "component_id"}),
    	joinColumns={@JoinColumn(name="item_id", referencedColumnName="id")}, 
    	inverseJoinColumns={@JoinColumn(name="component_id", referencedColumnName="id")})
	public Collection<Component> components;
}