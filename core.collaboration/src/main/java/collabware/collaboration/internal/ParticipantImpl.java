package collabware.collaboration.internal;

import static collabware.utils.Asserts.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

public class ParticipantImpl implements Participant {

	private final User user;
	private final Set<Collaboration> ownedCollaborations = new HashSet<Collaboration>();
	private final Set<Collaboration> participatingCollaborations = new HashSet<Collaboration>();
	private final List<Participant> contacts = new ArrayList<Participant>();

	public ParticipantImpl(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Set<Collaboration> getOwnedCollaborations() {
		return Collections.unmodifiableSet(ownedCollaborations);
	}

	void addOwnedCollaboration(CollaborationImpl collaborationImpl) {
		assertNotNull("collaborationImpl", collaborationImpl);
		
		ownedCollaborations.add(collaborationImpl);
	}
	
	public String toString() {
		return String.format("Participant {name='%s'}", this.user.getDisplayName());
	}

	@Override
	public List<Participant> getContacts() {
		return Collections.unmodifiableList(contacts);
	}

	@Override
	public void addContact(Participant contact) {
		assertNotNull("contact", contact);

		contacts.add(contact);
	}

	@Override
	public Set<Collaboration> getParticipatingCollaborations() {
		return Collections.unmodifiableSet(participatingCollaborations);
	}

	@Override
	public void addParticipatingCollaboration(Collaboration collaboration) {
		assertNotNull("collaboration", collaboration);
		
		if ( ! participatingCollaborations.contains(collaboration)) {
			participatingCollaborations.add(collaboration);		
			collaboration.addParticipant(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ParticipantImpl other = (ParticipantImpl) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
