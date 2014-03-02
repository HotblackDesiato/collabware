package collabware.collaboration.client;

import java.util.List;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;

/**
 * Listener to be notified about changes to the client's connection endpoint.
 * 
 */
public interface ClientEndpointListener {

	/**
	 * Requests the client to initialize. This is in response to
	 * {@link ClientEndpoint#join(String)}.
	 * 
	 * @param initSequence
	 *            The {@link ComplexOperation} that initializes the
	 *            {@link Document}
	 * @param participants
	 *            The list of {@link ClientParticipant}s
	 * @param localParticipant
	 *            The participant who owns the client.
	 */
	void initialize(ComplexOperation initSequence, List<ClientParticipant> participants, ClientParticipant localParticipant);

	/**
	 * Called when initialization failed. This is in response to
	 * {@link ClientEndpoint#join(String)}.
	 * 
	 * @param errorMessage
	 */
	void initializeFailed(String errorMessage);

	/**
	 * Client was disconnected from server.
	 */
	void disconnected();

	/**
	 * Client is requested to update its {@link Document} with a given
	 * {@link ComplexOperation}.
	 * 
	 * @param op
	 * @param clientSequenceNumber
	 * @param serverSequenceNumber
	 */
	void update(ComplexOperation op, int clientSequenceNumber, int serverSequenceNumber);

	/**
	 * A participant was added to this collaboration.
	 * 
	 * @param addedParticipant
	 */
	void participantAdded(ClientParticipant addedParticipant);

	/**
	 * Initialize replay. This is in response to
	 * {@link ClientEndpoint#fetchReplaySequence()}.
	 * 
	 * @param init
	 *            The {@link ComplexOperation} to initialize the
	 *            {@link Document}
	 * @param sequence
	 *            the list of {@link ComplexOperation}s that can be replayed.
	 */
	void initReplay(ComplexOperation init, List<ComplexOperation> sequence);
}