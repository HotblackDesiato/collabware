package collabware.collaboration.internal;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import collabware.api.operations.context.Context;
import collabware.collaboration.Client;
import collabware.collaboration.Participant;

/**
 * Maintains a list of  {@link Participant}s and {@link Client}s registered for a {@link CollaborationImpl}
 *
 */
class ParticipantRegistry {

	/**
	 * allocated client numbers.
	 */
	private final BitSet clientNumbers = new BitSet();
	private final Map<ClientProxy, Participant> clients = new HashMap<ClientProxy, Participant>();
	private final CollaborationImpl collaboration;

	ParticipantRegistry(CollaborationImpl collaboration) {
		this.collaboration = collaboration;
	}
	
	/**
	 * Registers a {@code participant} with this collaboration.
	 * @param participant
	 * @return the client through which changes can be made on behalf of {@code participant}.
	 */
	Client registerParticipant(Participant participant) {
		Context initialContext = collaboration.getState().asContextForClient(pickFreeClientNumber());
		ClientProxy clientProxy = new ClientProxy(collaboration, participant, initialContext);
		clients.put(clientProxy, participant);
		return clientProxy;
	}

	/**
	 * Unregisters a client, after which no more changes can be made through the client.
	 * @param client 
	 */
	void unregisterClient(Client client) {
		clients.remove(client);
		((ClientProxy) client).invalidate();
		releaseClientNumber(client.getClientNumber());
	}

	private void releaseClientNumber(int clientNumber) {
		clientNumbers.clear(clientNumber);
	}

	/**
	 * 
	 * @return set of registered participants.
	 */
	Set<Participant> getRegisteredParticipants() {
		return new HashSet<Participant>(clients.values());
	}

	/**
	 * 
	 * @return set of registered clients.
	 */
	Set<ClientProxy> getRegisteredClients() {
		return clients.keySet();
	}
	
	private int pickFreeClientNumber() {
		int freeClientNumber = clientNumbers.nextClearBit(0);
		clientNumbers.set(freeClientNumber);
		return freeClientNumber;
	}
}
