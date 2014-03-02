package collabware.collaboration;

import collabware.api.operations.ComplexOperation;

/**
 * A client makes changes to a collaboration on behalf of a participant.
 *@see Collaboration#join(Participant)
 */
public interface Client {
	
	public interface ClientListener {
		void updateClient(ComplexOperation change, ClientContext context);
	}
	
	void addListener(ClientListener listener);
	void removeListener(ClientListener listener);
	
	public interface ClientContext {
		
		int getClientSequenceNumber();
		int getServerSequenceNumber();
		
		ClientContext incrementingClient();
		ClientContext incrementingServer();
		
		boolean greaterThanOrEqual(ClientContext c);
	}
	
	/**
	 * 
	 * @return {@code true} if and only if this client is joined.
	 */
	boolean isJoined();
	
	/**
	 * Leave the collaboration for which this client is registered.
	 */
	void leave();

	/**
	 * 
	 * @param serverSequenceNumber
	 * @param op
	 * @throws ConflictingOperationsException if {@code op} is in conflict with another concurrent operation.
	 */
	void applyChangeToCollaboration(int serverSequenceNumber, ComplexOperation op) throws ConflictingOperationsException;

	int getClientNumber();
	int getClientSequenceNumber();
	int getServerSequenceNumber();
	
	/**
	 * 
	 * @return the collaboration for which this client is registered.
	 */
	Collaboration getCollaboration();
	
	/**
	 * 
	 * @return the participant on whose behalf changes are made.
	 */
	Participant getParticipant();
	
	
	ClientContext getCurrentContext();
}
