package collabware.collaboration.network.messages;

import collabware.collaboration.client.ClientEndpointListener;

public interface ServerMessage {

	String getClientId();

	void dispatchTo(ClientEndpointListener clientEndpointListener);

}
