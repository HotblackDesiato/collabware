package collabware.collaboration.network.messages;

import java.util.ArrayList;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Participant;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ParticipantImpl;
import collabware.userManagement.User;

public class JoinResponse implements ServerMessage {

	private final String clientId;
	private final ComplexOperation initSequence;
	private final List<collabware.collaboration.client.ClientParticipant> participants;
	private final collabware.collaboration.client.ClientParticipant localParticipant;

	public JoinResponse(String clientId, ComplexOperation op, List<Participant> participants, Participant localParticipant) {
		this.clientId = clientId;
		this.initSequence = op;
		this.participants = createClientParticipants(participants);
		this.localParticipant = asClientParticipant(localParticipant);
	}

	private List<collabware.collaboration.client.ClientParticipant> createClientParticipants(List<Participant> participants2) {
		List<collabware.collaboration.client.ClientParticipant> clientParticipants = new ArrayList<collabware.collaboration.client.ClientParticipant>();
		for (Participant p : participants2) {
			clientParticipants.add(asClientParticipant(p));
		}
		return clientParticipants;
	}

	private ParticipantImpl asClientParticipant(Participant p) {
		User user = p.getUser();
		return new ParticipantImpl(user.getUserName(), "", user.getDisplayName());
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public void dispatchTo(ClientEndpointListener clientEndpointListener) {
		clientEndpointListener.initialize(initSequence, participants, localParticipant);
	}

}
