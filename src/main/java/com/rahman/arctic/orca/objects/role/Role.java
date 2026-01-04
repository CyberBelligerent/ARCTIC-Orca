package com.rahman.arctic.orca.objects.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Role information to be stored into a database
 * @author SGT Rahman
 *
 */
@Entity
@Getter @Setter
@Table(uniqueConstraints={
		@UniqueConstraint(columnNames = {"role_role_name"})
})
public class Role {

	@Id
	@Column(name = "role_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role_role_name", nullable = false, unique = true)
	private UserRole role;
	
}