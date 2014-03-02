package collabware.collaboration.server;

import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;

public interface ServerEndpoint {
		
	void addServerEndpointListener(ServerEndpointListener listener);

	void initializeClient(String clientId, ComplexOperation op,	List<Participant> participants, Participant localParticipant);

	void sendUpdate(String clientId, ComplexOperation change, int clientSequenceNumber, int serverSequenceNumber);

	void sendParticipantAdded(Collaboration collaboration, Participant addedParticipant);

	void initializeClient(String clientId, String string);

	void acknowledge(String clientId, int clientSequenceNumber,	int serverSequenceNumber);

}