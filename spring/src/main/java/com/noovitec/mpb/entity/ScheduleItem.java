package com.noovitec.mpb.entity;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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
	LocalTime startTime;

	@JsonIgnoreProperties(value = { "scheduleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "line_id", referencedColumnName = "id")
	private Line line;

	@JsonIgnoreProperties(value = { "scheduleItems", "itemComponents", "saleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	@JsonIgnoreProperties(value = { "scheduleItems" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "schedule_id", referencedColumnName = "id")
	private Schedule schedule;

	@JsonIgnoreProperties(value = { "scheduleItem" }, allowSetters = true)
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "schedule_item_id")
	private Collection<Production> productions = new HashSet<Production>();

	// Transient
	@Transient
	Long totalProduced;

	public Long getTotalProduced() {
		Long units = 0L;
		for (Production production : this.getProductions()) {
			units += production.getUnitsProduced();
		}
		return units;
	}
}