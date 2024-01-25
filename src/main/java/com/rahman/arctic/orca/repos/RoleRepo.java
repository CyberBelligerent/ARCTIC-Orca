package com.rahman.arctic.orca.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rahman.arctic.orca.objects.role.Role;
import com.rahman.arctic.orca.objects.role.UserRole;

/**
 * Spring Boot Repository (Database) management for User Roles
 * @author SGT Rahman
 *
 */
@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
	/**
	 * Grabs a role based off of the enum class UserRole
	 * @param enum UserRole to obtain
	 * @return Role stored in Database
	 */
	Role findByRole(UserRole role);
	boolean existsByRole(UserRole role);
}