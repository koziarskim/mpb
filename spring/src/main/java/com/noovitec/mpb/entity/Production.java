package com.noovitec.mpb.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//@EntityListeners(ProductionListener.class)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Production extends BaseEntity{

	private LocalTime finishTime;
	private Long unitsProduced;
	private Long people;

	@ManyToOne()
	@JoinColumn(name = "schedule_event_id", referencedColumnName = "id")
	private ScheduleEvent scheduleEvent;
	
}