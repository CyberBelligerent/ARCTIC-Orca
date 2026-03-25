package com.rahman.arctic.orca.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rahman.arctic.orca.objects.ExercisePermission;
import com.rahman.arctic.orca.objects.role.ExerciseRole;

@Repository
public interface ExercisePermissionRepo extends JpaRepository<ExercisePermission, String> {

	boolean existsByUsernameAndExerciseIdAndRoleIn(String username, String exerciseId, List<ExerciseRole> roles);

	boolean existsByUsernameAndExerciseIdAndRole(String username, String exerciseId, ExerciseRole role);

	List<ExercisePermission> findByUsername(String username);

	void deleteByUsernameAndExerciseIdAndRole(String username, String exerciseId, ExerciseRole role);

}
