package collabware.collaboration.internal.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.Contextualizer;
import collabware.api.transform.CollisionException;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.Command;
import collabware.collaboration.client.Session;
import collabware.collaboration.client.SessionListener;
import collabware.collaboration.client.replay.Replay;
import collabware.collaboration.internal.client.replay.ReplayImpl;
import collabware.collaboration.internal.context.VectorBasedContext;
import collabware.transformer.TransformationProvider;
import collabware.utils.Formats;

public class ClientImpl implements ClientEndpointListener, Session {
	
	private static final Logger logger = Logger.getLogger("collabware.collaboration.client.ClientImpl"); 
	
	public enum ConnectionState {
		DISCONNECTED,
		CONNECTING,
		CONNECTED;
	}

	static final int CLIENT = 0;
	static final int SERVER = 1;
	
	private Contextualizer contextualizer = Contextualizer.INSTANCE;
	private List<ClientParticipant> participants = new ArrayList<ClientParticipant>();
	private Set<SessionListener> listener = new HashSet<SessionListener>();

	private ConnectionState connectionState = ConnectionState.DISCONNECTED;
	private ClientParticipant localParticipant;
	private ClientState clientState;
	private ReplayImpl replay;
	private ClientEndpoint clientEndpoint;
	
	public ClientImpl(ClientEndpoint clientEndpoint, TransformationProvider transformationProvider) {
		this.clientEndpoint = clientEndpoint;
		clientEndpoint.addClientEndpointListener(this);
		this.clientState = new ClientState(clientEndpoint, new ClientTransformer(transformationProvider));
		this.replay = new ReplayImpl(clientEndpoint, clientState);
	}
	
	@Override
	public Document getDocument() {
		return clientState.getDocument();
	}
	
	@Override
	public void applyChange(Command command) {
		if (connectionState == ConnectionState.CONNECTED) {
			clientState.applyChange(command);
		} else {
			throw log(new IllegalStateException("Can only apply Change when joined."));
		}
	}
	
	@Override
	public void join(String collaborationId) {
		if (connectionState == ConnectionState.DISCONNECTED) {
			connectionState=ConnectionState.CONNECTING;
			logger.info(Formats.format("Joining collaboration {0}.", collaborationId));
			clientState.join(collaborationId);
			replay.enable();
		} else {
			throw log(new IllegalStateException("Can only join when disconnected."));
		}
	}

	@Override
	public void initialize(ComplexOperation initSequence, List<ClientParticipant> participants, ClientParticipant localParticipant) {
		logger.info("initializing document with : " + initSequence.toString());
		if (connectionState == ConnectionState.CONNECTING) {
			doInitialize(initSequence, participants, localParticipant);
			notifyJoined();
			logger.info("Collaboration joined.");
		} else {
			throw log(new IllegalStateException("Unexpected call to initialize."));
		}
	}

	private void doInitialize(ComplexOperation initSequence, List<ClientParticipant> participants, ClientParticipant localParticipant) {
		this.localParticipant = localParticipant;
		this.participants  = new ArrayList<ClientParticipant>(participants);
		this.clientState.init(initSequence);
		connectionState=ConnectionState.CONNECTED;
	}

	private void notifyJoined() {
		logger.info("Notifying listeners : ");
		for (SessionListener l: this.listener) {
			try {
				l.joined();
			} catch (Exception e) {
				logger.severe("Exception while notifying listeners: " + e.getMessage());
			}
		}
	}
	
	@Override
	public void initializeFailed(String errorMessage) {
		// TODO Auto-generated method stub
	}

	@Override
	public void disconnected() {
		logger.info("Disconnected");
		clientState.clear();
		participants.clear();
		localParticipant = null;
		connectionState = ConnectionState.DISCONNECTED;
		notifyDisconnected();
	}

	private void notifyDisconnected() {
		logger.info("Notifying listeners : ");
		for (SessionListener l: this.listener) {
			try {
				l.disconnected();
			} catch (Exception e) {
				logger.severe("Exception while notifying listeners: " + e.getMessage());
			}
		}
	}

	@Override
	public void update(ComplexOperation op,	int clientSequenceNumber, int serverSequenceNumber) {
		logger.info("Updating document with operation " + op.toString());
		try {
			doUpdate(op, clientSequenceNumber, serverSequenceNumber);
		} catch (Exception e) {
			throw log(new RuntimeException("Exception while updating: ", e));
		}
		logger.info("Updating finished.");
	}

	private void doUpdate(ComplexOperation op, int clientSequenceNumber, int serverSequenceNumber) throws CollisionException, OperationApplicationException {
		ContextualizedComplexOperation change = contextualize(op, clientSequenceNumber, serverSequenceNumber);
		if (replay.isReplaying()) {
			replay.queueChange(change);
		} else {
			clientState.update(change);
		}
	}

	private ContextualizedComplexOperation contextualize(ComplexOperation op, int clientSequenceNumber, int serverSequenceNumber) {
		Context ctx = new VectorBasedContext(SERVER, new int[]{clientSequenceNumber, serverSequenceNumber});
		return contextualizer.createComplexOperation(ctx, op);
	}

	@Override
	public List<ClientParticipant> getParticipants() {
		return Collections.unmodifiableList(participants);
	}

	@Override
	public void participantAdded(ClientParticipant addedParticipant) {
		participants.add(addedParticipant);
		for (SessionListener l : listener) {
			l.participantAdded(addedParticipant);
		}
	}

	@Override
	public void disconnect() {
		logger.info("Disconnecting");
		clientEndpoint.disconnect();
	}

	@Override
	public void addSessionListener(SessionListener l) {
		listener.add(l);
	}

	@Override
	public ClientParticipant getLocalParticipant() {
		return localParticipant;
	}

	@Override
	public void initReplay(ComplexOperation init, List<ComplexOperation> sequence) {
		replay.initReplay(init, sequence);
	}
	
	private static <T extends Exception> T log(T e) {
		logger.severe(e.getMessage());
		return e;
	}

	@Override
	public Replay replay() {
		return replay;
	}
}
