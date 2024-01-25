package com.rahman.arctic.orca.objects.role;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Role information to be stored into a database
 * @author SGT Rahman
 *
 */
@Entity
@Data
public class Role {

	@Id
	@Column(name = "role_id")
	private String id;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	public Role() {
		id = UUID.randomUUID().toString();
	}
	
	public Role(UserRole r) {
		this();
		r = role;
	}
	
}