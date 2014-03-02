package collabware.collaboration.network.messages;

import collabware.collaboration.Participant;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ParticipantImpl;
import collabware.userManagement.User;

public class ParticipantAddedMessage implements ServerMessage {

	private String clientId;
	private collabware.collaboration.client.ClientParticipant addedParticipant;

	public ParticipantAddedMessage(String clientId, Participant addedParticipant) {
		this.clientId = clientId;
		User user = addedParticipant.getUser();
		this.addedParticipant = new ParticipantImpl(user.getUserName(), "", user.getDisplayName());
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public void dispatchTo(ClientEndpointListener clientEndpointListener) {
		clientEndpointListener.participantAdded(addedParticipant);
	}

}
