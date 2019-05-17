package com.noovitec.mpb.entity;

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
public class ScheduleEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	Long unitsScheduled;
	LocalTime startTime;

	@JsonIgnoreProperties(value = { "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;

	@JsonIgnoreProperties(value = { "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "schedule_id", referencedColumnName = "id")
	private Schedule schedule;

	@JsonIgnoreProperties(value = { "scheduleEvent" }, allowSetters = true)
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "schedule_event_id")
	private Collection<Production> productions = new HashSet<Production>();

	@JsonIgnoreProperties(value = { "saleItems", "purchaseSales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_id", referencedColumnName = "id")
	private SaleItem saleItem;

	// Transient
	@Transient
	Long totalProduced;
	@Transient
	int unitsShort = 0;

	public Long getTotalProduced() {
		Long units = 0L;
		for (Production production : this.getProductions()) {
			units += production.getUnitsProduced()==null?0:production.getUnitsProduced();
		}
		return units;
	}
}