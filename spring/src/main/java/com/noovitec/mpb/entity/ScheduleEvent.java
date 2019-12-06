package com.noovitec.mpb.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

	LocalTime scheduleTime;
	LocalTime startTime;
	LocalTime finishTime;
	Long unitsScheduled = 0L;
	Long unitsProduced = 0L;

	@JsonIgnoreProperties(value = { "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;

	@JsonIgnoreProperties(value = { "scheduleEvents" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "schedule_id", referencedColumnName = "id")
	private Schedule schedule;

	@JsonIgnoreProperties(value = { "scheduleEvent" }, allowSetters = true)
	@OneToMany()
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
	private Long performance = 0L;
	
	public Long getPerformance() {
		BigDecimal perf = BigDecimal.ZERO;
		Long time = this.getTotalTime();
		if(time > 0 && this.unitsProduced > 0) {
			perf = BigDecimal.valueOf(this.unitsProduced).divide(BigDecimal.valueOf(time), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(3600));
		}
		return perf.longValue();
	}
	
	@Transient
	private BigDecimal efficency;
	
	public BigDecimal getEfficiency() {
		BigDecimal eff = BigDecimal.ZERO;
		eff = BigDecimal.valueOf(this.getPerformance())
				.divide(BigDecimal.valueOf(this.getSaleItem().getItem().getPerformance()), 6, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));
		
		return eff.setScale(1, RoundingMode.HALF_UP);
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
	
	public void updateUnits() {
		this.unitsProduced = 0L;
		for (Production p : this.getProductions()) {
			this.unitsProduced += p.getUnitsProduced();
		}
	}

}