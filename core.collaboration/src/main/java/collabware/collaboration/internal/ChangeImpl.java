package collabware.collaboration.internal;

import java.util.Date;

import collabware.collaboration.Change;
import collabware.collaboration.Collaboration;
import collabware.userManagement.User;

class ChangeImpl implements Change {

	private final String description;
	private final Collaboration collaboration;
	private final User user;
	private final Date dateTime;
	
	ChangeImpl(String description, Collaboration collaboration, User user) {
		this.description = description;
		this.collaboration = collaboration;
		this.user = user;
		this.dateTime = new Date();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Collaboration getCollaboration() {
		return collaboration;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Date getDateTime() {
		return dateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((collaboration == null) ? 0 : collaboration.hashCode());
		result = prime * result	+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeImpl other = (ChangeImpl) obj;
		if (collaboration == null) {
			if (other.collaboration != null)
				return false;
		} else if (!collaboration.equals(other.collaboration))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	

}
