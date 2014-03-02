package collabware.collaboration;

import java.util.Date;
import java.util.List;
import java.util.Set;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;

public interface Collaboration {

	/**
	 * 
	 * @return String uniquely identifying this collaboration.
	 */
	String getId();

	/**
	 * 
	 * @return the owner (i.e. the creator) of this collaboration
	 */
	Participant getOwner();

	/**
	 * Joins this collaboration. A participant can join a collaboration multiple times resulting in multiple, different clients.
	 *  
	 * @param participant
	 * @return The Client through which changes can be made to this collaboration
	 */
	Client join(Participant participant);

	/**
	 * 
	 * @return the list of participants.
	 */
	List<Participant> getParticipants();

	/**
	 * Invalidates the client and frees resources. No more changes can be made through this client.
	 * 
	 * @param client
	 */
	void leave(Client client);
	
	Document getDocument();

	/**
	 * 
	 * @return the name of this collaboration
	 */
	String getName();

	/**
	 * 
	 * @return the content type of this collaboration.
	 */
	String getType();
	
	/**
	 * 
	 * @return the set of active (i.e. joined) participants.
	 */
	Set<Participant> getActiveParticipants();

	/**
	 * Adds a participant to this collaboration.
	 * 
	 * @param participant
	 */
	void addParticipant(Participant participant);
	
	
	/**
	 * Adds a listener that gets notified about changes made to this collaboration.
	 * 
	 * @param listener
	 */
	void addChangeListener(ChangeListener listener);

	/**
	 * 
	 * @return sequence of operations that allow to replay changes made to this collaboration.
	 */
	ReplaySequence getReplaySequence();
	
	/**
	 * 
	 * @return The ComplexOperation that recreates the document. 
	 */
	ComplexOperation getInitializeOperation();
	
	/**
	 * 
	 */
	Date getCreatedOn();

	Change getLastChange();
}
