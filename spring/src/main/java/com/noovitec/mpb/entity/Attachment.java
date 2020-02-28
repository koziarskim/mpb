package com.noovitec.mpb.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment extends BaseEntity {

	private String name;
	private String type;
	
	@JsonIgnoreProperties(value = { "data" }, allowSetters = true)
	@ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "doc_content_id", referencedColumnName = "id")
	private DocContent docContent;


}