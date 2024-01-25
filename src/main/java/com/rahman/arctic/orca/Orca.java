package com.rahman.arctic.orca;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.rahman.arctic.orca.repos"})
@EntityScan("com.rahman.arctic.orca.objects")
public class Orca {

	public Orca() {
		System.out.println("Enabling Service: Orca");
	}
	
}