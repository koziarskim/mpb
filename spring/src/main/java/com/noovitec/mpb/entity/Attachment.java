package com.noovitec.mpb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore // Don't send it to client.
	@Column(insertable = true, updatable = false)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] data;

}