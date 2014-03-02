package collabware.userManagement;

import static collabware.utils.Asserts.assertNotNull;

public class UserDetails {
	private final String userId;
	private final String password;
	private final String displayName;

	public UserDetails(String userId, String password, String displayName) {
		assertNotNull("userId", userId); 
		assertNotNull("password", password);
		assertNotNull("displayName", displayName);
		
		this.userId = userId;
		this.password = password;
		this.displayName = displayName;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public boolean equals(Object o) {
		if (o instanceof UserDetails) {
			UserDetails userDetails = (UserDetails)o;
			return userId.equals(userDetails .getUserId()) && password.equals(userDetails.getPassword()) && displayName.equals(userDetails.getDisplayName());
		} else { 
			return false;
		}
		
		}

}
	