package collabware.collaboration.server;

import java.util.HashMap;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Client;
import collabware.collaboration.Client.ClientContext;
import collabware.collaboration.Client.ClientListener;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;

public class Server implements ServerEndpointListener, ChangeListener {
	
	private CollaborationProvider collaborationProvider;
	private ServerEndpoint serverConnector;
	private HashMap<String, Client> idToClient = new HashMap<String, Client>();
	
	class ClientListenerImpl implements ClientListener {

		private final ServerEndpoint serverEndpoint;
		private final String clientId;

		public ClientListenerImpl(ServerEndpoint serverEndpoint, String clientId) {
			this.serverEndpoint = serverEndpoint;
			this.clientId = clientId;
		}
		
		@Override
		public void updateClient(ComplexOperation change, ClientContext context) {
			this.serverEndpoint.sendUpdate(clientId, change, context.getClientSequenceNumber(), context.getServerSequenceNumber());
		}
		
	}
	
	public Server(ServerEndpoint serverConnector, CollaborationProvider collaborationProvider) {
		this.serverConnector = serverConnector;
		this.collaborationProvider = collaborationProvider;
		this.collaborationProvider.addChangeListener(this);
		this.serverConnector.addServerEndpointListener(this);
	}

	@Override
	public void joinClient(String clientId, Participant p, String collaborationId) throws ServerException {
		assertClientNotRegistered(clientId);
		try {
			Collaboration collaboration = collaborationProvider.getCollaboration(collaborationId);
			Client joinedClient = collaboration.join(p);
			joinedClient.addListener(new ClientListenerImpl(serverConnector, clientId));
			idToClient.put(clientId, joinedClient);
			ComplexOperation op = collaboration.getInitializeOperation();
			serverConnector.initializeClient(clientId, op, collaboration.getParticipants(), p);
		} catch (NoSuchCollaborationException e) {
			serverConnector.initializeClient(clientId, "No such collaboration.");
		}
	}

	private void assertClientNotRegistered(String clientId) throws ServerException {
		if (this.idToClient.containsKey(clientId))
			throw new ServerException(String.format("Client '%s' is already registered.", clientId));
	}

	public Collaboration getCollaboration(String string) throws NoSuchCollaborationException {
		return collaborationProvider.getCollaboration(string);
	}

	@Override
	public void applyClientChange(String clientId, String collaborationId, ComplexOperation complexOperationImpl, int clientSequenceNumber,	int serverSequenceNumber) {
		Client client = idToClient.get(clientId);
		try {
			client.applyChangeToCollaboration(serverSequenceNumber, complexOperationImpl);
		} catch (ConflictingOperationsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serverConnector.acknowledge(clientId, client.getClientSequenceNumber(), client.getServerSequenceNumber());
	}

	@Override
	public void changeApplied(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void participantAdded(Collaboration collaboration, Participant addedParticipant) {
		serverConnector.sendParticipantAdded(collaboration, addedParticipant);
	}

	@Override
	public void leave(String clientId) {
		idToClient.get(clientId).leave();
	}
}