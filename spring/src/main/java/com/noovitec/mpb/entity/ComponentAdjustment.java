package com.noovitec.mpb.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class ComponentAdjustment extends BaseEntity {
	
	private static final long serialVersionUID = -2143229024849030526L;
	private long unitsAdjusted;
	private LocalDate date;
	private String reason;
	private String notes;

	@JsonIgnoreProperties(value = { "itemComponents", "purchaseComponents", "componentAdjustments" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "component_id", referencedColumnName = "id")
	private Component component;
}