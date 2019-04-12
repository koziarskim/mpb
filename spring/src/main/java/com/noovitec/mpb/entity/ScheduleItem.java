package com.noovitec.mpb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduleItem {

	@Id
	@GeneratedValue
	private Long id;
	Long unitsScheduled;
	Long unitsProduced;

	@JsonIgnoreProperties(value = { "scheduleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;

	@JsonIgnoreProperties(value = { "scheduleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "scheduleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "schedule_id", referencedColumnName = "id")
	private Schedule schedule;
}