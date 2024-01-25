package com.rahman.arctic.orca.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.rahman.arctic.orca.objects.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

/**
 * User entry to be stored in Database to obtain information such as OpenStack connection
 * username, password and permissions the user has
 * @author SGT Rahman
 *
 */
@Entity
@Data
public class RangeUser {

	@Id
	private String id;
	
	@Column(name = "user_username")
	private String username;
	private String name;
	private String password;
	private String projectId;
	private String tokenId;
	private Date passwordLastReset;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="userRoles", joinColumns = @JoinColumn(name="user_username"), inverseJoinColumns = @JoinColumn(name="role_id"))
	private List<Role> userRoles = new ArrayList<>();
	
	// This is used to check if the user is logging in with a new device
	@ElementCollection
	private Set<String> knownDevices = new HashSet<>();
	
	public RangeUser() {
		id = UUID.randomUUID().toString();
	}
	
	public RangeUser(String user) {
		this();
		username = user;
	}
	
}