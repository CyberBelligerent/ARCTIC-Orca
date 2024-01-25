package com.rahman.arctic.orca.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rahman.arctic.orca.objects.RangeUser;
/**
 * Spring Boot Repository (Database) management for RangeUsers
 * @author SGT Rahman
 *
 */

@Repository
public interface UserRepo extends JpaRepository<RangeUser, String> {
	/**
	 * Grabs a user by their username but ignoring all capitalization
	 * @param username Username to check
	 * @return Optional of RangeUser
	 */
	Optional<RangeUser> findByUsernameIgnoreCase(String username);
	
	/**
	 * Returns a boolean value for whether a user exists in the database by username ignoring case
	 * @param username Username to check
	 * @return true or false
	 */
	boolean existsByUsernameIgnoreCase(String username);
}