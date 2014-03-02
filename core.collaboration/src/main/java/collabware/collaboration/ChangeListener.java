package collabware.collaboration;

public interface ChangeListener {
	void changeApplied(Change change);
	void participantAdded(Collaboration collaboration, Participant addedParticipant);
}
