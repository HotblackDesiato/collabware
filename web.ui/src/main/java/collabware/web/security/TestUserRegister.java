package collabware.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collabware.userManagement.UserDetails;
import collabware.userManagement.UserManagement;
import collabware.userManagement.exception.DuplicateUserIdException;

@Component
public class TestUserRegister {
	private UserManagement userManagement;
		
	public void registerUsers() {
		try {
			userManagement.createUser(new UserDetails("justin.case", "1234", "Justin Case"));
			userManagement.createUser(new UserDetails("justin.time", "1234", "Justin Time"));
			userManagement.createUser(new UserDetails("doris.shutt", "1234", "Doris Shutt"));
			userManagement.createUser(new UserDetails("james.bond", "1234", "James Bond"));
			userManagement.createUser(new UserDetails("jason.bourne", "1234", "Jason Bourne"));
		} catch (DuplicateUserIdException e) {}
	}


	@Autowired
	public void setUserManagement(UserManagement userManagement) {
		this.userManagement = userManagement;
		registerUsers();
	}
}
