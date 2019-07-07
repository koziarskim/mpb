package com.noovitec.mpb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductionLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private LocalDate dateStarted;
	private LocalDate dateFinished;
	private LocalTime timeStarted;
	private LocalTime timeFinished;

	@JsonIgnoreProperties({ "itemComponents", "saleItems" })
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;
	
	@JsonIgnoreProperties(value = { "productionLine" }, allowSetters = true)
	@OneToMany()
	@JoinColumn(name = "production_line_id")
	private Collection<ProductionOutput> productionOutputs = new HashSet<ProductionOutput>();

	@Transient
	private Long totalProduced = 0L;
	
	public Long getTotalProduced() {
		Long units = 0L;
		for(ProductionOutput po: this.productionOutputs) {
			units += po.getUnits();
		}
		return units;
	}
}