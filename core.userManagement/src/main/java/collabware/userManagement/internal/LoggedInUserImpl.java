package collabware.userManagement.internal;

import static collabware.utils.Asserts.assertNotNull;
import collabware.userManagement.AuthenticatedUser;
import collabware.userManagement.User;

public class LoggedInUserImpl implements AuthenticatedUser {

	private final User user;
	private boolean loggedIn;

	public LoggedInUserImpl(User user) {
		assertNotNull("user", user);
		
		this.user = user;
		this.loggedIn = true;
	}

	public String getUserName() {
		return user.getUserName();
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void logOff() {
		loggedIn = false;
	}

	public String getDisplayName() {
		return user.getDisplayName();
	}

}
