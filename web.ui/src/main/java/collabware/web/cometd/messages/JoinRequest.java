package collabware.web.cometd.messages;

import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;

public class JoinRequest implements Request {
	private final String collaborationId;
	private final ServerSession requester;
	
	public JoinRequest(ServerSession remote, ServerMessage.Mutable message) {
		this.collaborationId = extractCollaborationIdFrom(message);
		this.requester = remote;
	}
	
	private String extractCollaborationIdFrom(ServerMessage.Mutable message) {
		return message.getChannelId().getSegment(2);
	}
	
	public String getCollaborationId() {
		return collaborationId;
	}

	public String getRequesterId() {
		return requester.getId();
	}

	public ServerSession getRequester() {
		return requester;
	}
}
