package com.noovitec.mpb.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.noovitec.mpb.trigger.ProductionListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@EntityListeners(ProductionListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Production {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private LocalTime finishTime;
	private Long unitsProduced;
	private Long people;

	@ManyToOne()
	@JoinColumn(name = "schedule_event_id", referencedColumnName = "id")
	private ScheduleEvent scheduleEvent;
}