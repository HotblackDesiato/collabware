package collabware.collaboration.network.messages;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.ClientEndpointListener;

public class UpdateMessage implements ServerMessage {

	private final String clientId;
	
	public String getClientId() {
		return clientId;
	}

	private ComplexOperation update;
	private int clientSequenceNumber;
	private int serverSequenceNumber;

	public UpdateMessage(String clientId, ComplexOperation change, int clientSequenceNumber, int serverSequenceNumber) {
		this.clientId = clientId;
		this.update = change;
		this.clientSequenceNumber = clientSequenceNumber;
		this.serverSequenceNumber = serverSequenceNumber;
	}

	@Override
	public void dispatchTo(ClientEndpointListener clientEndpointListener) {
		clientEndpointListener.update(update, clientSequenceNumber, serverSequenceNumber);
	}


}
