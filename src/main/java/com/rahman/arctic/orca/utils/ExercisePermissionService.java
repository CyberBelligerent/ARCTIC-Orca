package com.rahman.arctic.orca.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rahman.arctic.orca.objects.ExercisePermission;
import com.rahman.arctic.orca.objects.role.ExerciseRole;
import com.rahman.arctic.orca.repos.ExercisePermissionRepo;

@Service
public class ExercisePermissionService {

	private final ExercisePermissionRepo repo;

	public ExercisePermissionService(ExercisePermissionRepo repo) {
		this.repo = repo;
	}

	public boolean isGlobalAdmin(ArcticUserDetails user) {
		return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
	}

	public boolean hasPermission(String username, String exerciseId, ExerciseRole required) {
		return repo.existsByUsernameAndExerciseIdAndRoleIn(username, exerciseId,
				List.of(required, ExerciseRole.RANGE_ADMIN));
	}

	public void grant(String username, String exerciseId, ExerciseRole role) {
		if (repo.existsByUsernameAndExerciseIdAndRole(username, exerciseId, role))
			return;
		ExercisePermission perm = new ExercisePermission();
		perm.setUsername(username);
		perm.setExerciseId(exerciseId);
		perm.setRole(role);
		repo.save(perm);
	}

	@Transactional
	public void revoke(String username, String exerciseId, ExerciseRole role) {
		repo.deleteByUsernameAndExerciseIdAndRole(username, exerciseId, role);
	}

	public Set<String> getAccessibleExerciseIds(String username) {
		return repo.findByUsername(username).stream().map(ExercisePermission::getExerciseId)
				.collect(Collectors.toSet());
	}

}
