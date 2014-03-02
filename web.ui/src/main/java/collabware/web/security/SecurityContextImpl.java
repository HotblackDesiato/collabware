package collabware.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextImpl implements SecurityContext {
	private static final long serialVersionUID = -5705348140737969544L;

	private SecurityContext getContext() {
		return org.springframework.security.core.context.SecurityContextHolder.getContext();
	}
	
	@Override
	public Authentication getAuthentication() {
		return getContext().getAuthentication();
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		getContext().setAuthentication(authentication);
	}
}
