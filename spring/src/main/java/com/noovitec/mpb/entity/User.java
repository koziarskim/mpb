package com.noovitec.mpb.entity;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "system_user")
public class User {

	@Id
	@GeneratedValue
	private Long id;
	private String number; // employee name
	private String name; // userName, loginName, etc.
	private String password;
	private String firstName;
	private String lastName;

	@JsonIgnoreProperties(value = { "users" }, allowSetters=true)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Collection<Role> roles = new HashSet<Role>();

	@Transient
	private String fullName;
	
	public String getFullName() {
		return this.firstName + " "+ this.lastName;
	}
	
}