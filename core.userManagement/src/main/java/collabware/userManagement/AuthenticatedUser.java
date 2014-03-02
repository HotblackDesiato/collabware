package collabware.userManagement;

public interface AuthenticatedUser extends User {

	boolean isLoggedIn();

	void logOff();

}
