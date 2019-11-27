package com.noovitec.mpb.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//@EntityListeners(ScheduleEventListener.class)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleEvent extends BaseEntity{

	Long unitsScheduled = 0L;
	LocalTime scheduleTime;
	LocalTime startTime;
	LocalTime finishTime;
	Long unitsProduced = 0L; //Updated by ProductionListener.

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

	//TODO: Is this used?
	@Transient
	int unitsShort = 0;

	@Transient
	boolean eventCompleted = false;

	public boolean isEventCompleted() {
		Long produced = this.getUnitsProduced();
		Long scheduled = this.getUnitsScheduled();
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
				start = this.getStartTime();
			}
			totalSecs += ChronoUnit.SECONDS.between(start, p.getFinishTime());
			//Set for next start
			start = p.getFinishTime();
		}
		return totalSecs;
	}
	
	public void updateUnits() {
		this.unitsProduced = 0L;
		for(Production p: this.getProductions()) {
			this.unitsProduced += p.getUnitsProduced();
		}
	}
	
	
//	public Long getUnitsProduced() {
//		this.unitsProduced = 0L;
//		for(Production p : this.getProductions()) {
//			this.unitsProduced += p.getUnitsProduced();
//		}
//		return this.unitsProduced;
//	}
//	
	
}