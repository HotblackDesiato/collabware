package collabware.collaboration;

import java.util.List;
import java.util.Set;

import collabware.userManagement.User;

public interface Participant {

	public User getUser();

	public Set<Collaboration> getOwnedCollaborations();

	public Set<Collaboration> getParticipatingCollaborations();

	public List<Participant> getContacts();
	
	public void addContact(Participant contact);

	public void addParticipatingCollaboration(Collaboration collaboration);

}
