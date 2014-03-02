package collabware.collaboration;

import java.util.Date;

import collabware.userManagement.User;

public interface Change {
	String getDescription();
	Collaboration getCollaboration();
	User getUser();
	Date getDateTime();
}
