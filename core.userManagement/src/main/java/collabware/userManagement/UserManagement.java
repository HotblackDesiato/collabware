package collabware.userManagement;

import java.util.Collection;

import collabware.userManagement.exception.AuthenticationException;
import collabware.userManagement.exception.DuplicateUserIdException;
import collabware.userManagement.exception.NoSuchUserException;

public interface UserManagement {

	User createUser(UserDetails parameterObject) throws DuplicateUserIdException;

	User getUser(String userName) throws NoSuchUserException;

	AuthenticatedUser login(String userName, String password) throws AuthenticationException;

	Collection<? extends User> getRegisteredUsers();
	
}
