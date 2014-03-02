package collabware.web.cometd.messages;

import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;

public class LeaveRequest implements Request {

	private final ServerSession remote;

	public LeaveRequest(ServerSession remote, Mutable message) {
		this.remote = remote;
	}

	public ServerSession getRequester() {
		return remote;
	}

	public String getRequesterId() {
		return remote.getId();
	}

}
