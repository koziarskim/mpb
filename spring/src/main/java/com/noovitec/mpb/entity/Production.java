package com.noovitec.mpb.entity;

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
public class Production extends BaseEntity {

	private LocalTime finishTime;
	private long unitsProduced = 0;
	private long people = 0;

	@ManyToOne()
	@JoinColumn(name = "schedule_event_id", referencedColumnName = "id")
	private ScheduleEvent scheduleEvent;

	@Transient private Long preUnitsProduced;

	public Long getPreUnitsProduced() {
		if(this.preUnitsProduced == null) {
			this.preUnitsProduced = this.unitsProduced;
		}
		return this.preUnitsProduced;
	}
	
}