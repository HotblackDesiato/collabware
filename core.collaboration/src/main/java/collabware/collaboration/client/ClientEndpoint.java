package collabware.collaboration.client;

import collabware.api.operations.ComplexOperation;

/**
 * The client's connection endpoint.
 * 
 */
public interface ClientEndpoint {

	/**
	 * Adds a listener to this endpoint to get notified about incoming changes.
	 * @param listener
	 */
	void addClientEndpointListener(ClientEndpointListener listener);

	/**
	 * Joins a collaboration with a given id. Joining a collaboration is not complete before 
	 * {@link ClientEndpointListener#initialize(ComplexOperation, java.util.List, ClientParticipant)} was called.
	 * @param id
	 */
	void join(String id);

	/**
	 * Sends a update (i.e. {@link ComplexOperation} to the server.
	 * @param complexOperation the operation that was applied.
	 * @param clientSequenceNumber number of client operations performed so far.
	 * @param serverSequenceNumber number of server operations received so far.
	 */
	void sendUpdate(ComplexOperation complexOperation, int clientSequenceNumber, int serverSequenceNumber);

	String getClientId();

	/**
	 * Disconnect from the server.
	 */
	void disconnect();

	/**
	 * Requests the replay sequence to be sent. 
	 * Once the sequence was received, {@link ClientEndpointListener#initReplay(ComplexOperation, java.util.List)} is called.
	 */
	void fetchReplaySequence();
}