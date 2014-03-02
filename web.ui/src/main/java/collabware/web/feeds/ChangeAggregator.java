package collabware.web.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

@Component("ChangeAggregator")
public class ChangeAggregator implements ChangeListener, InitializingBean {

	private final Map<Collaboration, DocumentChangeList> changesPerCollaboration = new HashMap<Collaboration, DocumentChangeList>();
	private static final int maxEntries = 3;

	@Autowired
	private CollaborationProvider collaborationProvider;

	@Override
	public void afterPropertiesSet() throws Exception {
		collaborationProvider.addChangeListener(this);
	}

	@Override
	public void changeApplied(Change change) {
		DocumentChangeList documentChangeList = changesPerCollaboration.get(change.getCollaboration());
		if (documentChangeList == null) {
			documentChangeList = new DocumentChangeList(change.getCollaboration(), maxEntries);
			changesPerCollaboration.put(change.getCollaboration(), documentChangeList);
		}
		documentChangeList.add(change);
	}

	public List<DocumentChangeList> getAllChangesForUser(User p) {
		List<DocumentChangeList> changes = new ArrayList<DocumentChangeList>();
		for (DocumentChangeList change : changesPerCollaboration.values()) {
			if (change.affects(p))
				changes.add(change);
		}
		return changes;
	}

	@Override
	public void participantAdded(Collaboration collaboration, Participant addedParticipant) {
		// Not interested in change in participants.
	}

}
