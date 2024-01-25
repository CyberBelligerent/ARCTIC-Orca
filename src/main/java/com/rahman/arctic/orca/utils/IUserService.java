package com.rahman.arctic.orca.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rahman.arctic.orca.objects.RangeUser;
import com.rahman.arctic.orca.repos.UserRepo;

/**
 * Spring Boot Service in-charge of grabbing the users details from an attempted log-in for RangeUser
 * @author SGT Rahman
 *
 */
@Service
public class IUserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		RangeUser iu = userRepo.findByUsernameIgnoreCase(username).orElse(null);
		if(iu == null) throw new UsernameNotFoundException("User not found");
		return new IUserDetails(iu);
	}
	
}