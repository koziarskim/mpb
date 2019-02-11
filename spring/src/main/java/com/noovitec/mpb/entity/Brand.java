package com.noovitec.mpb.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Brand {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	private String name;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id")
	private Collection<Component> components;

}