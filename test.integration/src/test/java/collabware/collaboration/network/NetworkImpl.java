package collabware.collaboration.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.internal.ParticipantImpl;
import collabware.collaboration.network.messages.ClientMessage;
import collabware.collaboration.network.messages.JoinRequest;
import collabware.collaboration.network.messages.JoinResponse;
import collabware.collaboration.network.messages.ParticipantAddedMessage;
import collabware.collaboration.network.messages.ServerMessage;
import collabware.collaboration.network.messages.UpdateMessage;
import collabware.collaboration.network.messages.UpdateRequest;
import collabware.collaboration.server.ServerEndpoint;
import collabware.collaboration.server.ServerEndpointListener;
import collabware.collaboration.server.ServerException;
import collabware.userManagement.UserDetails;
import collabware.userManagement.internal.UserImpl;

public class NetworkImpl {
	public class ServerEndpointImpl implements ServerEndpoint {
		private ServerEndpointListener listener;
		private List<ServerMessage> toClientsQueue = new LinkedList<ServerMessage>();
		
		@Override
			
		public void addServerEndpointListener(ServerEndpointListener listener) {
			this.listener = listener;
		}

		@Override
		public void initializeClient(String clientId, ComplexOperation op, List<Participant> participants, Participant localParticipant) {
			toClientsQueue.add(new JoinResponse(clientId, op, new ArrayList<Participant>(participants), localParticipant));
		}

		void requestJoin(String clientId, Participant p, String collaborationId) {
			try {
				listener.joinClient(clientId, p, collaborationId);
			} catch (ServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public ServerEndpointListener getListener() {
			return listener;
		}
		
		public void dispatchMessages() {
			List<ServerMessage> queue = toClientsQueue;
			toClientsQueue = new ArrayList<ServerMessage>();
			for (ServerMessage m : queue) {
				m.dispatchTo(clientEndpoints.get(m.getClientId()).getListener());
			}
		}

		@Override
		public void sendUpdate(String clientId, ComplexOperation change, int clientSequenceNumber, int serverSequenceNumber) {
			toClientsQueue.add(new UpdateMessage(clientId, change, clientSequenceNumber, serverSequenceNumber));
			
		}

		@Override
		public void sendParticipantAdded(Collaboration collaboration, Participant addedParticipant) {
			for (ClientEndpointImpl c: clientEndpoints.values()) {
				toClientsQueue.add(new ParticipantAddedMessage(c.clientId, addedParticipant));				
			}
		}

		@Override
		public void initializeClient(String clientId, String string) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void acknowledge(String clientId, int clientSequenceNumber, int serverSequenceNumber) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private int clientCount = 0;
	public class ClientEndpointImpl implements ClientEndpoint {
		private final String clientId = "" + (clientCount++);
		private ClientEndpointListener listener;
		private String collaborationId;
		private Participant p = new ParticipantImpl(new UserImpl(new UserDetails(clientId, "1234", "Justin Case")));
		private List<ClientMessage> toServerQueue = new LinkedList<ClientMessage>();
		
		@Override
		public void addClientEndpointListener(ClientEndpointListener listener) {
			this.listener = listener;
		}
		
		@Override
		public void join(String id) {
			this.collaborationId = id;
			toServerQueue.add(new JoinRequest(clientId, p, id));
		}

		@Override
		public void sendUpdate(ComplexOperation complexOperationImpl, int clientSequenceNumber, int serverSequenceNumber) {
			System.out.println(String.format("Client '%s' sent operation %s", clientId, complexOperationImpl));
			toServerQueue.add(new UpdateRequest(complexOperationImpl, collaborationId, clientId, clientSequenceNumber, serverSequenceNumber));
		}

		public String getClientId() {
			return clientId;
		}

		public ClientEndpointListener getListener() {
			return listener;
		}
		
		void dispatchMessages() {
			List<ClientMessage> queue = toServerQueue;
			toServerQueue = new ArrayList<ClientMessage>();
			for (ClientMessage m : queue) {
				m.dispatchTo(serverEndpoint.listener);
			}
		}

		@Override
		public void disconnect() {
			listener.disconnected();
		}

		@Override
		public void fetchReplaySequence() {
			throw new UnsupportedOperationException();
		}
	}

	private final HashMap<String, ClientEndpointImpl> clientEndpoints = new HashMap<String, ClientEndpointImpl>();
	private final ServerEndpointImpl serverEndpoint = new ServerEndpointImpl();
	
	public ClientEndpoint createClientConnector() {
		ClientEndpointImpl endPoint = new ClientEndpointImpl();
		clientEndpoints.put(endPoint.getClientId(), endPoint);
		return endPoint;
	}

	public ServerEndpoint getServerConnector() {
		return serverEndpoint;
	}
		
	public void dispatchNow() {
		for (ClientEndpointImpl endpoint: clientEndpoints.values()) {
			endpoint.dispatchMessages();
		}
		serverEndpoint.dispatchMessages();
	}

}