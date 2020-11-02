package com.noovitec.mpb.entity;

import java.math.BigDecimal;
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
public class Packaging extends BaseEntity {

	private static final long serialVersionUID = -6014743735048004494L;
	private String name; //Package name
	private String type; //PDQ, MasterCarton, etc.
	private int casePack = 1;
	private BigDecimal caseHeight;
	private BigDecimal caseWidth;
	private BigDecimal caseDepth;
	private BigDecimal caseWeight;
	private BigDecimal palletWeight;
	private int ti = 1; // number of cases in single layer on pallet.
	private int hi = 1; // number of layers on pallet.
	private BigDecimal warehouseCost;
	private BigDecimal packageCost;
	private BigDecimal totalPackagingCost;
	
	@JsonIgnoreProperties(value = { "packaging" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "packaging_id")
	private Collection<ItemPackaging> itemPackagings = new HashSet<ItemPackaging>();

}