package com.noovitec.mpb.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Season {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private int prefix;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "season_id")
	private Collection<Item> items;

}