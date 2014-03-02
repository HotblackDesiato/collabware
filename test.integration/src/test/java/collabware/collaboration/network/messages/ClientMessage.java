package collabware.collaboration.network.messages;

import collabware.collaboration.server.ServerEndpointListener;

public interface ClientMessage {

	void dispatchTo(ServerEndpointListener listener);

}
