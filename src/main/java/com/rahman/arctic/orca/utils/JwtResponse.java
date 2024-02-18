package com.rahman.arctic.orca.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtResponse implements Serializable {
	private static final long serialVersionUID = -4995189361080360956L;
	private String token;
}