package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemComponent {

	@Id
	@GeneratedValue
	private Long id;
	private Integer units;

	@JsonIgnoreProperties(value={ "itemComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value={ "itemComponents" }, allowSetters=true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;
}