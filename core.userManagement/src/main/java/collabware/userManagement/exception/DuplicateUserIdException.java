package collabware.userManagement.exception;

public class DuplicateUserIdException extends Exception {

	public DuplicateUserIdException(String userName) {
		super(String.format("A user with the user name '%s' already exits.", userName));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1709399351109694790L;

}
