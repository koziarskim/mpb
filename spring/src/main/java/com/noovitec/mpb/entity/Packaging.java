package com.noovitec.mpb.entity;

import java.math.BigDecimal;

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
	public static enum TYPE {
		RSC_MC("RSC-MC"), 
		PDQ_HSC("PDQ-HSC"),
		HSC_DRC("HSC-DRC");
		private String label;
		public String label() { 
	        return this.label; 
	    }
	    private TYPE(String label) { 
	        this.label = label; 
	    } 
	}
	private String hologram;
	private String name; //Package name
	private String type; //PDQ, MasterCarton, etc.
	private int casePack = 1;
	private BigDecimal caseHeight;
	private BigDecimal caseLength;
	private BigDecimal caseWidth;
	private BigDecimal caseWeight;
	private BigDecimal palletWeight;
	private int ti = 1; // number of cases in single layer on pallet.
	private int hi = 1; // number of layers on pallet.
	private BigDecimal warehouseCost;
	private BigDecimal packageCost;
	private BigDecimal totalPackagingCost;
	
	@ManyToOne()
	@JoinColumn(name = "attachment_id", referencedColumnName = "id")
	private Attachment attachment;
	
	@Transient
	private String label;
	
	public String getLabel() {
		return this.getName() + " ("+Packaging.TYPE.valueOf(this.getType()).label()+")";
	}
	
//	@JsonIgnoreProperties(value = { "packaging" }, allowSetters = true)
//	@OneToMany()
//	@JoinColumn(name = "packaging_id")
//	private Collection<ItemPackaging> itemPackagings = new HashSet<ItemPackaging>();

}