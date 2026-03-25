package com.rahman.arctic.orca.objects;

import com.rahman.arctic.orca.objects.role.ExerciseRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Grants a specific ExerciseRole to a user for a single exercise.
 */
@Entity
@Data
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "username", "exercise_id", "role" })
})
public class ExercisePermission {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "permission_id")
	private String id;

	@Column(nullable = false)
	private String username;

	@Column(name = "exercise_id", nullable = false)
	private String exerciseId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExerciseRole role;

}
