package com.rahman.arctic.orca.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rahman.arctic.orca.objects.RangeUser;

/**
 * Security Class to grab information and permissions about a RangeUser
 * @author SGT Rahman
 *
 */
public class IUserDetails implements UserDetails {
	
	private static final long serialVersionUID = 5655820609682347075L;
	private RangeUser user;
	
	public IUserDetails(RangeUser iu) {
		user = iu;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> auths = new ArrayList<>();
		user.getUserRoles().forEach(e -> {
			auths.add(new SimpleGrantedAuthority(e.getRole().toString()));
		});
		return auths;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	public Date getLastPasswordReset() {
		return user.getPasswordLastReset();
	}
	
	public Set<String> getDeviceHistory() {
		return user.getKnownDevices();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}