package com.noovitec.mpb.entity;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	private Long unitsProduced;
	private Long people;

	@ManyToOne()
	@JoinColumn(name = "schedule_event_id", referencedColumnName = "id")
	private ScheduleEvent scheduleEvent;

}