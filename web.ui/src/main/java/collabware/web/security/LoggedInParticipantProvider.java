package collabware.web.security;

import org.springframework.stereotype.Component;

import collabware.collaboration.Participant;

@Component
public interface LoggedInParticipantProvider {
	
	Participant getLoggedinParticipant();
	
}
