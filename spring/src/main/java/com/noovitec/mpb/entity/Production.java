package com.noovitec.mpb.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Production {

	@Id
	@GeneratedValue
	private Long id;
	private LocalDate finishTime;
	private Long unitsProduced;

	@ManyToOne()
	@JoinColumn(name = "schedule_item_id", referencedColumnName = "id")
	private ScheduleItem scheduleItem;
}