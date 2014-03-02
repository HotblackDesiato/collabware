package collabware.collaboration.network.messages;

import collabware.collaboration.Participant;
import collabware.collaboration.server.ServerEndpointListener;
import collabware.collaboration.server.ServerException;

public class JoinRequest implements ClientMessage {

	private final String clientId;
	private final Participant participant;
	private final String collaborationId;

	public JoinRequest(String clientId, Participant p, String id) {
		this.clientId = clientId;
		this.participant = p;
		this.collaborationId = id;
		
	}
	
	@Override
	public void dispatchTo(ServerEndpointListener listener) {
		try {
			listener.joinClient(clientId, participant, collaborationId);
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

}
