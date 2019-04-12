package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Schedule { //Daily Schedule.

	@Id
	@GeneratedValue
	private Long id;
	private Date date; //Day
	
	@JsonIgnoreProperties(value = { "schedule" }, allowSetters = true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "schedule_id")
	private Collection<ScheduleItem> scheduleItems = new HashSet<ScheduleItem>();

}