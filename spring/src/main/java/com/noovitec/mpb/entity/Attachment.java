package com.noovitec.mpb.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Transient;

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
	private String mimeType;
	private Long docContentId;
	private String filePath;
	
	@Transient
	@JsonIgnore
	private DocContent docContent;

	public String getFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return this.getName() + "-" + sdf.format(timestamp) +"."+ this.getMimeType();
	}
}