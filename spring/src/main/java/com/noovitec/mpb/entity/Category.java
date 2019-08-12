package com.noovitec.mpb.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreationTimestamp
	private LocalDateTime created;
	@UpdateTimestamp
	private LocalDateTime updated;
	private String name;
	private String prefix;
	private String type;

//	@JsonIgnore
//	@OneToMany(fetch = FetchType.LAZY)
//	@JoinColumn(name = "category_id")
//	private Collection<Component> components = new HashSet<Component>();
//
//	//Not sure why JsonIgnore is needed here but without it there is error.
//	@JsonIgnore
//	@OneToMany(fetch = FetchType.LAZY)
//	@JoinColumn(name = "category_id")
//	private Collection<Item> items = new HashSet<Item>();

}