package collabware.collaboration.client;

import java.util.List;

import collabware.api.document.Document;
import collabware.collaboration.client.replay.Replay;

/** A collaboration session. 
 *
 */
public interface Session {

	/** 
	 * 
	 * @return The document to be collaborated on. Is empty until joined. 
	 */
	Document getDocument();

	/**
	 * Changes the document by applying a command.
	 * 
	 * @param command
	 * @throws IllegalStateException if not joined or disconnected.
	 */
	void applyChange(Command command) throws IllegalStateException;

	/** 
	 * Joins a collaboration.
	 * @param collaborationId
	 * @throws IllegalStateException if already connected.
	 */
	void join(String collaborationId) throws IllegalStateException;

	/**
	 * 
	 * @return The list of participants. May be empty if not joined.
	 */
	List<ClientParticipant> getParticipants();

	/**
	 * Disconnects the session from the collaboration. 
	 * Also empties the document as well as the list of participants.
	 */
	void disconnect();

	/**
	 * Add a listener to be notified about changes to the session.
	 * @param l
	 */
	void addSessionListener(SessionListener l);

	/**
	 * 
	 * @return The participant who initiated the session.
	 */
	ClientParticipant getLocalParticipant();

	/**
	 * 
	 * @return The replay controls.
	 */
	Replay replay();
	
}