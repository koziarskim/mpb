package com.noovitec.mpb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Receiving {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private String number;
	private String container;
	private int units;
	private int unitsReserved;
	private LocalDate shippedDate;
	private LocalDate etaDate;
	private LocalDate receivedDate;

	@JsonIgnoreProperties(value = { "receivings" }, allowSetters = true)
	@ManyToOne()
	@JoinColumn(name = "purchaseComponent_id", referencedColumnName = "id")
	private PurchaseComponent purchaseComponent;

	public String getNumber() {
		if(this.number == null) {
			this.number = this.getId().toString();
		}
		return this.number;
	}
}