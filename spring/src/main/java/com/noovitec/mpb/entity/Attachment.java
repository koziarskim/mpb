package com.noovitec.mpb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String type;
	@JsonIgnore //Don't send it to client.
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] data;

}