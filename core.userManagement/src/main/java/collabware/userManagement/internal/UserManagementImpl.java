package collabware.userManagement.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import collabware.userManagement.AuthenticatedUser;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
import collabware.userManagement.UserManagement;
import collabware.userManagement.exception.AuthenticationException;
import collabware.userManagement.exception.DuplicateUserIdException;
import collabware.userManagement.exception.NoSuchUserException;

public class UserManagementImpl implements UserManagement {

	private final Map<String, UserImpl> knownUsers = new HashMap<String, UserImpl>();
	
	public User createUser(UserDetails userDetails) throws DuplicateUserIdException {
		assertNotNull("userDetails", userDetails); 
		assertUserNameIsNotTaken(userDetails.getUserId());
		
		UserImpl user = new UserImpl(userDetails);
		knownUsers.put(userDetails.getUserId(), user);
		return user;
	}

	public User getUser(String userName) throws NoSuchUserException {
		assertNotNull("userName", userName);
		
		if (knownUsers.containsKey(userName)) 
			return knownUsers.get(userName);
		else
			throw new NoSuchUserException();
	}

	public AuthenticatedUser login(String userName, String password) throws AuthenticationException {
		assertNotNull("userName", userName); assertNotNull("password", password);
		
		UserImpl user = knownUsers.get(userName);
		if (user != null && user.hasPassword(password))
			return new LoggedInUserImpl(user);			
		else 
			throw new AuthenticationException();			
	}

	private void assertUserNameIsNotTaken(String userName) throws DuplicateUserIdException {
		assertNotNull("userName", userName);
		
		if (knownUsers.containsKey(userName)) 
			throw new DuplicateUserIdException(userName);
	}

	@Override
	public Collection<? extends User> getRegisteredUsers() {
		return Collections.unmodifiableCollection(knownUsers.values());
	}
}
