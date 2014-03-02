package collabware.userManagement.internal;

import static collabware.utils.Asserts.assertNotNull;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
public class UserImpl implements User {
	private final String userId;
	private final String password;
	private final String displayName;

	public UserImpl(UserDetails userDetails) {
		assertNotNull("userDetails", userDetails);
		
		this.userId = userDetails.getUserId();
		this.password = userDetails.getPassword();
		this.displayName = userDetails.getDisplayName();
	}

	public String getUserName() {
		return userId;
	}

	public boolean hasPassword(String password) {
		assertNotNull("password", password);
		
		return this.password.equals(password);
	}

	public String getDisplayName() {
		return displayName;
	}

}
