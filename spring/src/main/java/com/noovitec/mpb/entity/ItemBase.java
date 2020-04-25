package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
public class ItemBase extends BaseEntity {

	private static final long serialVersionUID = -4733808922106577980L;
	private String number;
	private String name;

	@JsonIgnoreProperties(value = { "base" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "item_id")
	private Collection<Item> items = new HashSet<Item>();

}