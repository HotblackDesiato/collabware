package collabware.web.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.stereotype.Component;

import collabware.userManagement.AuthenticatedUser;
import collabware.userManagement.UserManagement;

@Component("myAuthenticationProvider")
public class Authenticator implements AuthenticationProvider {

	@Autowired
	private UserManagement userManagement;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			String userName = authentication.getPrincipal().toString();
			String password = authentication.getCredentials().toString();
			AuthenticatedUser authenticatedUser = userManagement.login(userName, password);
			return new UsernamePasswordAuthenticationToken(authenticatedUser, password, asList("ROLE_USER"));
		} catch (collabware.userManagement.exception.AuthenticationException e) {
			throw new BadCredentialsException("Wrong username or password");
		}
	}

	private Collection<GrantedAuthority> asList(String ...roles) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role:roles) {
			authorities.add(new GrantedAuthorityImpl(role));
		}
		return authorities;
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return true;
	}

}
