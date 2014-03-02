package collabware.userManagement.exception;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -6460600831935267866L;

	public AuthenticationException() {
		super("Invalid username or password.");
	}

}
