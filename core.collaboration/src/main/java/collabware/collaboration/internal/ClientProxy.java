package collabware.collaboration.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.collaboration.Client;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.Participant;

/**
 * This is the server's proxy for a remote client.
 *
 */
class ClientProxy implements Client {
	
	private static class ClientContextImpl implements ClientContext {

		private final int clientSequenceNumber;
		private final int serverSequenceNumber;
		
		ClientContextImpl(int clientSequenceNumber, int serverSequenceNumber) {
			this.clientSequenceNumber = clientSequenceNumber;
			this.serverSequenceNumber = serverSequenceNumber;
		}

		@Override
		public int getClientSequenceNumber() {
			return clientSequenceNumber;
		}

		@Override
		public int getServerSequenceNumber() {
			return serverSequenceNumber;
		}

		@Override
		public ClientContext incrementingClient() {
			return new ClientContextImpl(clientSequenceNumber+1, serverSequenceNumber);
		}

		@Override
		public ClientContext incrementingServer() {
			return new ClientContextImpl(clientSequenceNumber, serverSequenceNumber+1);
		}

		@Override
		public boolean greaterThanOrEqual(ClientContext c) {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
		
	}
	
	private static final Logger logger = Logger.getLogger("collabware.collaboration.internal.ClientProxy"); 
	
	private final ModifyableCollaboration collaboration;
	private final ClientStateToContextMapping clientStateToContext;
	private final int clientNumber;
	private final Participant participant;
	
	private int lastServerSequenceNumber = 0;
	private boolean isJoined = true;
	private final Set<ClientListener> listeners = new HashSet<Client.ClientListener>();
	private ClientContext clientContext = new ClientContextImpl(0, 0);
	
	ClientProxy(ModifyableCollaboration collaboration, Participant participant, Context initialContext) {
		this.collaboration = collaboration;
		this.participant = participant;
		this.clientNumber = initialContext.getClientNumber();
		this.clientStateToContext = new ClientStateToContextMapping(initialContext);
	}

	@Override
	public synchronized void applyChangeToCollaboration(int serverSequenceNumber, ComplexOperation complexOperation) {
		logger.info(String.format("Applying operation '%s' with serverSequenceNumber=%s.", serverSequenceNumber, complexOperation.getDescription()));
		
		assertSequenceNumberWithinBounds(serverSequenceNumber);
		Context context = clientStateToContext.getContextForServerState(serverSequenceNumber);
		try {
			ContextualizedComplexOperation operation = new ContextualizedComplexOperationImpl(context, complexOperation);
			collaboration.apply(operation, this.participant.getUser());
			clientStateToContext.addClientOperation(operation, serverSequenceNumber);
			lastServerSequenceNumber = serverSequenceNumber;
			clientContext = clientContext.incrementingClient();
		} catch (ConflictingOperationsException e) {
			// Nothing to be done. 
			// Client state is not changed.
		}
	}

	private void assertSequenceNumberWithinBounds(int serverSequenceNumber) {
		int minSequenceNumber = lastServerSequenceNumber;
		int maxSequenceNumber = clientStateToContext.getNumberOfServerStates()-1; // sequence numbers are zero-based
		if (serverSequenceNumber < minSequenceNumber || serverSequenceNumber > maxSequenceNumber) 
			throw new IllegalArgumentException(String.format("Server sequence (%s) number must not decrease over time. Current bounds are: min=%s, max=%s.",serverSequenceNumber, minSequenceNumber,maxSequenceNumber));
	}

	public synchronized void sendToClient(ContextualizedComplexOperation contextualizedComplexOperation) {
		logger.info(String.format("Sending operation '%s' with context %s to client %s of %s.", contextualizedComplexOperation.getDescription(), contextualizedComplexOperation.getContext(), clientNumber , participant));

		Context serverContext = contextualizedComplexOperation.getSelfIncludingContext();
		clientStateToContext.addNewServerState(serverContext);
		for (ClientListener listener: listeners) {
			listener.updateClient(contextualizedComplexOperation.getDecoratedOperation(), clientContext);
		}
		clientContext = clientContext.incrementingServer();
	}


	@Override
	public boolean isJoined() {
		return isJoined;
	}

	@Override
	public int getClientNumber() {
		return clientNumber;
	}

	@Override
	public collabware.collaboration.Collaboration getCollaboration() {
		return collaboration;
	}
	
	void invalidate() {
		isJoined = false;
	}

	@Override
	public int getClientSequenceNumber() {
		return clientContext.getClientSequenceNumber();
	}

	@Override
	public int getServerSequenceNumber() {
		return clientContext.getServerSequenceNumber();
	}

	@Override
	public synchronized void addListener(ClientListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeListener(ClientListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void leave() {
		collaboration.leave(this);
	}

	@Override
	public Participant getParticipant() {
		return participant;
	}
	
	public String toString() {
		return String.format("Client {clientNumber='%s', collaboration=%s, participant=%s}", this.clientNumber, this.collaboration, this.participant);
	}

	@Override
	public ClientContext getCurrentContext() {
		return clientContext;
	}
}