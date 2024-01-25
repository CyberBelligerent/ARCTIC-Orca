package com.rahman.arctic.orca.objects;

import lombok.Data;

/**
 * Data-transfer object to convert from JSON String into RangeUser
 * @author SGT Rahman
 *
 */
@Data
public class UserDTO {
	private String name;
	private String username;
	private String password;
	private String role = "user";
	private String projectId;
}