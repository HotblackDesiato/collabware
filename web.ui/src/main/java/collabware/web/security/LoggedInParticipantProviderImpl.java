package collabware.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

@Component
public class LoggedInParticipantProviderImpl implements	LoggedInParticipantProvider {

	@Autowired
	private CollaborationProvider collaborationProvider;
	
	@Override
	public Participant getLoggedinParticipant() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return collaborationProvider.getParticipant(user);
	}

}
