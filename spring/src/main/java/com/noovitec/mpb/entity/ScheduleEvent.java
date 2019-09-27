package com.noovitec.mpb.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
	LocalTime scheduleTime;
	LocalTime startTime;
	LocalTime finishTime;

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

	//TODO: Is this used?
	@Transient
	int unitsShort = 0;

	@Transient
	Long totalProduced = 0L;

	public Long getTotalProduced() {
		Long units = 0L;
		for (Production production : this.getProductions()) {
			units += production.getUnitsProduced()==null?0L:production.getUnitsProduced();
		}
		return units;
	}

	@Transient
	boolean eventCompleted = false;

	public boolean isEventCompleted() {
		Long produced = this.getTotalProduced();
		Long scheduled = this.getUnitsScheduled()==null?0L:this.getUnitsScheduled();
		return produced >= scheduled;
	}
	
	@Transient
	private Long totalTime = 0L; //In seconds.
	
	public Long getTotalTime() {
		if(this.finishTime!=null) {
			this.totalTime = ChronoUnit.SECONDS.between(this.startTime, this.finishTime);
		}
		return totalTime;
	}
	
	@Transient
	private Long unitsPending = 0L;
	
	public Long getUnitsPending() {
		Long units = 0L;
		for(Production p: this.getProductions()) {
			units += p.getUnitsProduced();
		}
		return this.unitsScheduled==null?0L:this.unitsScheduled - units;
	}
	
	public Long getDurationSeconds() {
		Long totalSecs = 0L;
		if(this.getStartTime()==null || this.getProductions().size()==0) {
			return totalSecs;
		}
		LocalTime start = null;
		List<Production> productions = new ArrayList<Production>(this.getProductions());
		productions.sort((h1, h2) -> h1.getFinishTime().compareTo(h2.getFinishTime()));
		for(Production p: productions) {
			if(start == null) {
				start = p.getScheduleEvent().getStartTime();
			}
			totalSecs += ChronoUnit.SECONDS.between(start, p.getFinishTime());
			//Set for next start
			start = p.getFinishTime();
		}
		return totalSecs;
	}
	
}