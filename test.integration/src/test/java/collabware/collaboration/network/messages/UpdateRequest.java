package collabware.collaboration.network.messages;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.server.ServerEndpointListener;
import collabware.collaboration.server.ServerException;

public class UpdateRequest implements ClientMessage {


	private final ComplexOperation complexOperation;
	private final String collaborationId;
	private final String clientId;
	private int clientSequenceNumber;
	private int serverSequenceNumber;

	public UpdateRequest(ComplexOperation complexOperationImpl, String collaborationId, String clientId, int clientSequenceNumber, int serverSequenceNumber) {
		this.complexOperation = complexOperationImpl;
		this.collaborationId = collaborationId;
		this.clientId = clientId;
		this.clientSequenceNumber = clientSequenceNumber;
		this.serverSequenceNumber = serverSequenceNumber;
	}

	@Override
	public void dispatchTo(ServerEndpointListener listener) {
		try {
			listener.applyClientChange(clientId, collaborationId, complexOperation, clientSequenceNumber, serverSequenceNumber);
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}
}
