package collabware.userManagement.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import collabware.userManagement.AuthenticatedUser;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
import collabware.userManagement.UserManagement;
import collabware.userManagement.exception.AuthenticationException;
import collabware.userManagement.exception.DuplicateUserIdException;
import collabware.userManagement.exception.NoSuchUserException;

public class TestUserManagement {
	private static final String WRONG_PASSWORD = "wrongPassword";
	private static final String PASSWORD = "password";
	private static final String USER_ID = "userName";

	@Test
	public void createUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		
		User user = um.createUser(new UserDetails(USER_ID, PASSWORD, "User Name"));
		
		assertNotNull(user);
		assertEquals(USER_ID, user.getUserName());
		assertEquals("User Name", user.getDisplayName());
	}

	@Test(expected=DuplicateUserIdException.class)
	public void createDuplicateUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		um.createUser(new UserDetails(USER_ID, PASSWORD, ""));
		
		um.createUser(new UserDetails(USER_ID, "Blah", ""));
	}
	
	@Test
	public void getUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		
		User user = um.createUser(new UserDetails(USER_ID, PASSWORD, ""));
		
		assertEquals(user, um.getUser(USER_ID));
	}
	
	@Test(expected=NoSuchUserException.class)
	public void getNonexistingUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		
		um.getUser("doesNotExist");
	}
	
	@Test
	public void loginUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		User user = um.createUser(new UserDetails(USER_ID, PASSWORD, ""));
		
		AuthenticatedUser loggedInUser = um.login(USER_ID, PASSWORD);
		
		assertEquals(user.getUserName(), loggedInUser.getUserName());
		assertEquals(user.getDisplayName(), loggedInUser.getDisplayName());
		assertTrue(loggedInUser.isLoggedIn());
	}
	
	@Test(expected=AuthenticationException.class)
	public void loginUserWithWrongPassword() throws Exception {
		UserManagement um = new UserManagementImpl();
		um.createUser(new UserDetails(USER_ID, PASSWORD, ""));
		
		um.login(USER_ID, WRONG_PASSWORD);
	}
	
	@Test
	public void logOffUser() throws Exception {
		UserManagement um = new UserManagementImpl();
		um.createUser(new UserDetails(USER_ID, PASSWORD, ""));
		AuthenticatedUser loggedInUser = um.login(USER_ID, PASSWORD);
		
		loggedInUser.logOff();
		
		assertFalse(loggedInUser.isLoggedIn());
	}
}
