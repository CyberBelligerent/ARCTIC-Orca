package com.rahman.arctic.orca.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtRequest implements Serializable {
	private static final long serialVersionUID = -7719979069134089420L;
	private String username;
	private String password;
}