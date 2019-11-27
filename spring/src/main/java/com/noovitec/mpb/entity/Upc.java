package com.noovitec.mpb.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Upc extends BaseEntity {

	private String code;
	private boolean assigned;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "upc_id")
	private Collection<Item> items;
}