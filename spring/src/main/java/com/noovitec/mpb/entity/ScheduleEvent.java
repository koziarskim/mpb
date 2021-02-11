package com.noovitec.mpb.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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
public class ScheduleEvent extends BaseEntity {

	private static final long serialVersionUID = 920544982775913064L;
	LocalTime scheduleTime;
	LocalTime startTime;
	LocalTime finishTime;
	long unitsScheduled = 0;
	long unitsProduced = 0;
	private LocalDate date;

	@JsonIgnoreProperties(value = { "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;

	@JsonIgnoreProperties(value = { "scheduleEvents", "saleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_packaging_id", referencedColumnName = "id")
	private ItemPackaging itemPackaging;

	@JsonIgnoreProperties(value = { "scheduleEvent" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "schedule_event_id")
	private Collection<Production> productions = new HashSet<Production>();

	@JsonIgnoreProperties(value = { "saleItems", "purchaseSales" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "sale_item_id", referencedColumnName = "id")
	private SaleItem saleItem;

	@Transient
	boolean completed = false;

	public boolean isCompleted() {
		return this.unitsProduced >= this.unitsScheduled;
	}
	
	@Transient
	private Long totalTime = 0L; // In seconds.

	public Long getTotalTime() {
		if (this.finishTime == null) {
			return 0L;
		}
		return ChronoUnit.SECONDS.between(this.startTime, this.finishTime);
	}

	@Transient
	private Long unitsPending = 0L;

	public Long getUnitsPending() {
		return this.unitsScheduled - this.unitsProduced;
	}

	public Long getDurationSeconds() {
		Long totalSecs = 0L;
		if (this.getStartTime() == null || this.getProductions().size() == 0) {
			return totalSecs;
		}
		LocalTime start = null;
		List<Production> productions = new ArrayList<Production>(this.getProductions());
		productions.sort((h1, h2) -> h1.getFinishTime().compareTo(h2.getFinishTime()));
		for (Production p : productions) {
			if (start == null) {
				start = this.getStartTime();
			}
			totalSecs += ChronoUnit.SECONDS.between(start, p.getFinishTime());
			// Set for next start
			start = p.getFinishTime();
		}
		return totalSecs;
	}
	
	@Transient
	private long totalPeople = 0;
	
	public long getTotalPeople() {
		this.totalPeople = 0;
		for (Production p : this.getProductions()) {
			if(p.getPeople() > this.totalPeople) {
				this.totalPeople = p.getPeople();
			}
		}
		return this.totalPeople;
	}
	
	public void updateUnits() {
		this.unitsProduced = 0L;
		for (Production p : this.getProductions()) {
			this.unitsProduced += p.getUnitsProduced();
		}
	}

}