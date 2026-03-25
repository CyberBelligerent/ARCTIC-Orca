# Orca

Orca is ARCTIC's security layer. It handles JWT-based auth, user management, and Spring Security configuration. Sessions are fully stateless — no server-side session storage, just JWT tokens.

---

## What's in Here

**Entities (JPA)**
- `RangeUser` — the user entity. Has a username, name, BCrypt-encoded password, password reset date, and a list of roles.
- `Role` — maps a `UserRole` enum value to a JPA entity for storage.
- `UserRole` — the enum. Currently `ADMIN` and `USER`.

**Repos**
- `UserRepo` — JPA repo for `RangeUser`
- `RoleRepo` — JPA repo for `Role`

**Services / Utils**
- `ArcticUserService` — implements `UserDetailsService` for Spring Security. Loads users by username.
- `ArcticUserDetails` — the `UserDetails` wrapper Spring Security uses at runtime.
- `JwtTokenUtil` — generates and validates JWT tokens. Tokens include the username and the client's IP address. 24-hour expiry.
- `JwtRequestFilter` — `OncePerRequestFilter` that pulls the token out of the `Authorization` header or the `token` cookie and sets the security context.
- `JwtAuthenticationEntryPoint` — returns a 401 when someone hits a protected endpoint without a valid token.

**DTOs**
- `JwtRequest` — username + password for login
- `JwtResponse` — wraps the token string for the login response
- `UserDTO` — username, name, password, role for user creation

---

## Security Config

The filter chain in `Orca.java` currently has `.anyRequest().permitAll()`. This is intentional — RBAC is being redesigned. Don't touch it.

Password encoding uses BCrypt with a cost factor of 15.

---

## Known Gaps

- No token refresh mechanism. When your token expires you re-login.
- Roles exist in the database but aren't enforced yet at the endpoint level.
- JWT secret is a dev placeholder (`"ThisIsATest"`).
