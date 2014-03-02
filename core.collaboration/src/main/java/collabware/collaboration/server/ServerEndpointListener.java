package collabware.collaboration.server;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Participant;

public interface ServerEndpointListener {
	
	/**
	 * Joins a client on behalf of a participant to a collaboration.
	 * 
	 * @param clientId
	 * @param participant
	 * @param collaborationId
	 * @throws ServerException if client is already registered, collaboration does not exist or participant is not allowed to join collaboration.
	 */
	void joinClient(String clientId, Participant participant, String collaborationId) throws ServerException;

	/**
	 * 
	 * @param clientId
	 * @param collaborationId
	 * @param complexOperationImpl
	 * @param clientSequenceNumber
	 * @param serverSequenceNumber
	 * @throws ServerException
	 */
	void applyClientChange(String clientId, String collaborationId, ComplexOperation complexOperationImpl, int clientSequenceNumber, int serverSequenceNumber)  throws ServerException;

	void leave(String clientId);
}