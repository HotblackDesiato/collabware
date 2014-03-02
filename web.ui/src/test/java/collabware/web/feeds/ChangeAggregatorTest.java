package collabware.web.feeds;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import collabware.collaboration.Change;
import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;
import collabware.userManagement.User;

public class ChangeAggregatorTest {

	
	ChangeAggregator aggregator = new ChangeAggregator();
	
	@Test
	public void participantSeesChangesToHisCollaboration() throws Exception {
		User me = someUser();
		Collaboration myCollaboration = someCollaborationWithParticipants(me);
		Change change = someChange(someUser(), myCollaboration);
		
		aggregator.changeApplied(change);
		List<DocumentChangeList> allDocumentChangesForUser = aggregator.getAllChangesForUser(me);
		
		assertThat(allDocumentChangesForUser.size(), is(1));
		assertThat(allDocumentChangesForUser.get(0).getLatestChanges(), hasItem(change));
	}

	@Test
	public void participantSeesAllChangesToHisCollaboration() throws Exception {
		User me = someUser();
		Collaboration myCollaboration = someCollaborationWithParticipants(me);
		Change change1 = someChange(me, myCollaboration);
		Change change2 = someChange(me, myCollaboration);
		
		aggregator.changeApplied(change1);
		aggregator.changeApplied(change2);
		List<DocumentChangeList> allDocumentChangesForUser = aggregator.getAllChangesForUser(me);
		
		assertThat(allDocumentChangesForUser.size(), is(1));
		assertThat(allDocumentChangesForUser.get(0).getLatestChanges(), hasItem(change1));
		assertThat(allDocumentChangesForUser.get(0).getLatestChanges(), hasItem(change2));
	}

	@Test
	public void participantCannotSeeChangesToOtherCollaborations() throws Exception {
		User me = someUser();
		Collaboration myCollaboration = someCollaborationWithParticipants(me);
		Change change = someChange(someUser(), myCollaboration);
		
		aggregator.changeApplied(change);
		List<DocumentChangeList> allDocumentChangesForUser = aggregator.getAllChangesForUser(someUser());
		
		assertTrue(allDocumentChangesForUser.isEmpty());
	}

	@Test
	@Ignore
	public void documentChangesAreOrderedWithTheMostRecentChangeFirst() throws Exception {
		User me = someUser();
		Collaboration myCollaboration = someCollaborationWithParticipants(me);
		Collaboration myOtherCollaboration = someCollaborationWithParticipants(me);
		
		aggregator.changeApplied(someChange(someUser(), myCollaboration));
		aggregator.changeApplied(someChange(someUser(), myOtherCollaboration));
		aggregator.changeApplied(someChange(someUser(), myCollaboration));
		
		List<DocumentChangeList> allDocumentChangesForUser = aggregator.getAllChangesForUser(me);
		
		assertThat(allDocumentChangesForUser.size(), is(2));
		assertThat(allDocumentChangesForUser.get(0).getDocumentName(), is(myCollaboration.getName()));
		assertThat(allDocumentChangesForUser.get(1).getDocumentName(), is(myOtherCollaboration.getName()));
	}
	
	@Test
	public void aggregateChangesPerCollaborationForEachParticipant() throws Exception {
		User user1 = someUser();
		User user2 = someUser();
		User user3 = someUser();
		Collaboration col1 = someCollaborationWithParticipants(user1, user2, user3);
		Change change = someChange(user1, col1);
		
		aggregator.changeApplied(change);
		
		assertThat(aggregator.getAllChangesForUser(user1).get(0).getLatestChanges(),hasItem(change));
		assertThat(aggregator.getAllChangesForUser(user2).get(0).getLatestChanges(),hasItem(change));
		assertThat(aggregator.getAllChangesForUser(user3).get(0).getLatestChanges(),hasItem(change));
	}

	private Collaboration someCollaborationWithParticipants(User...users) {
		Collaboration col = createMock(Collaboration.class);
		List<Participant> participants= new ArrayList<Participant>();
		for (User u: users) {
			Participant p = createMock(Participant.class);
			expect(p.getUser()).andStubReturn(u);
			replay(p);
			participants.add(p);
		}
		
		expect(col.getParticipants()).andStubReturn(participants);
		expect(col.getName()).andStubReturn(UUID.randomUUID().toString());
		replay(col);
		return col;
	}

	private Change someChange(User user, Collaboration col) {
		Change change = createMock(Change.class);
		expect(change.getUser()).andStubReturn(user);
		expect(change.getCollaboration()).andStubReturn(col);
		replay(change);
		return change;
	}

	private Collaboration someCollaboration() {
		return someCollaborationWithParticipants();
	}

	private User someUser() {
		User user= createMock(User.class);
		expect(user.getDisplayName()).andStubReturn("someUser");
		replay(user);
		return user;
	}
}
