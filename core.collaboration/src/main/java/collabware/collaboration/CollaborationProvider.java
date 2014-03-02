package collabware.collaboration;

import java.util.Collection;

import collabware.userManagement.User;

/** 
 * Manages the life cycle of Collaborations.
 *
 */
public interface CollaborationProvider {

	Collaboration createCollaboration(CollaborationDetails details, Participant owner);

	Collaboration getCollaboration(String id) throws NoSuchCollaborationException;

	Collection<Collaboration> getAllCollaborations();

	Participant getParticipant(User user);

	boolean hasCollaboration(String collaborationId);

	void addChangeListener(ChangeListener listener);

}
