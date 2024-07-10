package com.rahman.arctic.orca;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.rahman.arctic.orca.objects.RangeUser;
import com.rahman.arctic.orca.objects.role.Role;
import com.rahman.arctic.orca.objects.role.UserRole;
import com.rahman.arctic.orca.repos.RoleRepo;
import com.rahman.arctic.orca.repos.UserRepo;

import jakarta.annotation.PostConstruct;

@Component
public class Dev {

	@Autowired
	private UserRepo ur;
	
	@Autowired
	private RoleRepo rr;
	
	@PostConstruct
	void createDefaultSettings() {
		Role r = new Role();
		r.setRole(UserRole.ADMIN);
		if(!rr.existsByRole(UserRole.ADMIN)) rr.save(r);
		
		RangeUser ru = new RangeUser("Admin");
		ru.setPassword(new BCryptPasswordEncoder().encode("1qaz2wsx!QAZ@WSX"));
		List<Role> roles = new ArrayList<>();
		roles.add(r);
		ru.setUserRoles(roles);
		
		if(!ur.existsByUsernameIgnoreCase("Admin")) {
			System.out.println("Admin user created");
			ur.save(ru);
		}
	}
	
}