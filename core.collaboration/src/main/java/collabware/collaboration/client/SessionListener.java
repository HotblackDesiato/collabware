package collabware.collaboration.client;

public interface SessionListener {
	
	void joined();
	
	void disconnected();
	
	void participantAdded(ClientParticipant participant);
}
