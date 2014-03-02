package collabware.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

public class AbstractProvider {

	@Autowired
	protected CollaborationProvider collaborationProvider;
	
	protected Participant getLoggedinParticipant() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return collaborationProvider.getParticipant(user);
	}

}
