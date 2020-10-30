package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
	private BigDecimal warehouseCost = new BigDecimal(12);
	private BigDecimal packageCost = new BigDecimal(12);
	
	@Transient
	private String label;
	
	public String getLabel() {
		return this.getName() + " ("+this.getType()+")";
	}
	
}