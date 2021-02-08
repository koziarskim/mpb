package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(schema="shared")
public class ComponentType extends BaseEntity {

	private static final long serialVersionUID = -4954896544515474005L;
	
	private String name;
	private String code;
	
	@JsonIgnoreProperties(value = { "componentTypes" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "category_id")
	private Category category;

}