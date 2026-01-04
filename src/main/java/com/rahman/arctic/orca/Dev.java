package com.rahman.arctic.orca;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${arctic.admin.username:admin}")
	private String adminUsername;
	
	@Value("${arctic.admin.password}")
	private String adminPassword;
	
	@PostConstruct
	void createDefaultSettings() throws Exception {
		if(adminPassword == null || adminPassword.isBlank()) {
			throw new IllegalStateException("ARCTIC admin password is required. Set arctic.admin.password / ARCTIC_ADMIN_PASSWORD");
		}
		
		Role r = rr.findByRole(UserRole.ADMIN);
		
		if(r == null) {
			r = new Role();
			r.setRole(UserRole.ADMIN);
			r = rr.save(r);
		}
		
		RangeUser ru = new RangeUser();
		ru.setUsername(adminUsername);
		ru.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
		List<Role> roles = new ArrayList<>();
		roles.add(r);
		ru.setUserRoles(roles);
		
		if(!ur.existsByUsernameIgnoreCase(adminUsername)) {
			System.out.println("Admin user created");
			ur.save(ru);
		}
	}
	
}