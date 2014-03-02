package collabware.collaboration.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.operations.context.DocumentState;
import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Client;
import collabware.collaboration.ConflictingOperationsException;
import collabware.collaboration.Participant;
import collabware.collaboration.ReplaySequence;
import collabware.userManagement.User;


public class CollaborationTest extends AbstractCollaborationTest {
	
	@Test
	public void ownerIsParticipant() throws Exception {		
		assertTrue(collaboration.getParticipants().contains(collaboration.getOwner()));
	}

	@Test
	public void joinParticipant() throws Exception {
		Participant alice = new ParticipantImpl(null);
		
		Client alicesClient = collaboration.join(alice);
		
		assertTrue(collaboration.getActiveParticipants().contains(alice));
		assertTrue(alicesClient.isJoined());
		assertEquals(0, alicesClient.getClientNumber());
		assertEquals(collaboration, alicesClient.getCollaboration());
	}
	
	@Test
	public void twoJoinedClientsHaveDifferentClientNumbers() throws Exception {
		Participant alice = new ParticipantImpl(null);
		Participant bob = new ParticipantImpl(null);
		
		Client alicesClient = collaboration.join(alice);		
		Client bobsClient = collaboration.join(bob);		

		assertEquals(0, alicesClient.getClientNumber());
		assertEquals(1, bobsClient.getClientNumber());
	}
		
	@Test
	public void doubleJoinParticipant() throws Exception {
		Participant bob = new ParticipantImpl(null);
		Client client1 = collaboration.join(bob);
		int distinctParticipants = collaboration.getParticipants().size();
		Client client2 = collaboration.join(bob);
		
		assertTrue(client1 != client2);
		assertEquals(distinctParticipants, collaboration.getParticipants().size());
	}
	
	@Test
	public void leave() throws Exception {
		Participant charlie = new ParticipantImpl(null);
		Client client = collaboration.join(charlie);
		collaboration.leave(client);
		
		assertFalse(client.isJoined());
		assertFalse(collaboration.getActiveParticipants().contains(charlie));
	}
	
	@Test
	public void participantIsStillActiveAfterOneOfHisTwoClientsLeft() throws Exception {
		Participant alice = new ParticipantImpl(null);
		
		Client client1 = collaboration.join(alice);
		Client client2 = collaboration.join(alice);
		collaboration.leave(client1);
		
		assertTrue(collaboration.getActiveParticipants().contains(alice));
		assertFalse(client1.isJoined());
		assertTrue(client2.isJoined());
	}
	
	@Test
	public void participantIsNotActiveAfterAllHisClientsLeft() throws Exception {
		Participant alice = new ParticipantImpl(null);
		
		Client client1 = collaboration.join(alice);
		Client client2 = collaboration.join(alice);
		collaboration.leave(client1);
		collaboration.leave(client2);
		
		assertFalse(collaboration.getActiveParticipants().contains(alice));
		assertFalse(client1.isJoined());
		assertFalse(client2.isJoined());
	}
	
	@Test
	public void clientNumberIsReusedAfterAClientLeft() throws Exception {
		Participant alice = new ParticipantImpl(null);
		
		@SuppressWarnings("unused")
		Client client1 = collaboration.join(alice);
		Client client2 = collaboration.join(alice);
		collaboration.leave(client2);
		Client client3 = collaboration.join(alice);

		assertEquals(1, client3.getClientNumber());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void participantsIsUnmodifyable() throws Exception {
		collaboration.getParticipants().remove(collaboration.getOwner());
	}
	
	@Test
	public void addingParticipantToCollaboration() {
		Participant bob = new ParticipantImpl(EasyMock.createMock(User.class));
		
		collaboration.addParticipant(bob);
		
		assertThat(collaboration.getParticipants().contains(bob), is(true));
		assertThat(bob.getParticipatingCollaborations().contains(collaboration), is(true));
	}
	@Test
	public void addingCollaborationToParticipatingCollaborations() {
		Participant bob = new ParticipantImpl(null);
		
		bob.addParticipatingCollaboration(collaboration);
		
		assertThat(collaboration.getParticipants().contains(bob), is(true));
		assertThat(bob.getParticipatingCollaborations().contains(collaboration), is(true));
	}

	@Test
	public void ownerIsAlsoParticipatingInCollaboration() {
		assertThat(collaboration.getOwner().getParticipatingCollaborations().contains(collaboration), is(true));
	}
	
	@Test
	public void addingParticipantNotifiesChangeListeners() {
		User user = createMock(User.class);
		expect(user.getDisplayName()).andStubReturn("Alice");
		Participant alice = new ParticipantImpl(user );
		
		ChangeListener listener = createMock(ChangeListener.class);
		listener.participantAdded(collaboration, alice);
		expectLastCall().once();
		replay(listener, user);
		
		collaboration.addChangeListener(listener);
		collaboration.addParticipant(alice);
		
		verify(listener);
	}
	
	@Test
	public void replaySequence() throws ConflictingOperationsException {
		CollaborationImpl modCol = (CollaborationImpl) collaboration;
		
		ContextualizedComplexOperation init = aComplexOperation(modCol.getState());
		modCol.apply(init, createMock(User.class));
		ContextualizedComplexOperation op1 = aComplexOperation(modCol.getState());
		modCol.apply(op1, createMock(User.class));
		ContextualizedComplexOperation op2 = aComplexOperation(modCol.getState());
		modCol.apply(op2, createMock(User.class));
		
		ReplaySequence seq = collaboration.getReplaySequence();
		
		assertThat(seq.getInit(), is((ComplexOperation)init));
		assertThat(seq.get(0), is((ComplexOperation)op1));
		assertThat(seq.get(1), is((ComplexOperation)op2));
	}
	
	@Test
	public void replaySequenceOfEmptyModelContainsIdOperation() {
		ReplaySequence seq = collaboration.getReplaySequence();
		
		assertThat(seq.getInit(), is(not(nullValue())));
		assertThat(seq.getInit().getPrimitiveOperations().size(), is(1));
		assertThat(seq.getInit().getPrimitiveOperations().get(0), is((PrimitiveOperation)NoOperation.NOP));
		assertThat(seq.length(), is(0));
	}

	private ContextualizedComplexOperation aComplexOperation(DocumentState state) {
		int n = 4;
		List<PrimitiveOperation> primitives = new ArrayList<PrimitiveOperation>(n);
		for (int i = 0; i < n; i ++) {
			primitives.add(NoOperation.NOP);
		}
		ComplexOperation complexOperation = new ComplexOperationImpl("", primitives);
		return new ContextualizedComplexOperationImpl(state.asContextForClient(0), complexOperation);
	}
	
	@Test
	public void initialLastChange() throws Exception {
		Change c = collaboration.getLastChange();
		assertThat(c.getUser(), is(collaboration.getOwner().getUser()));
	}

	@Test
	public void lastChangeAfterApplication() throws Exception {
		CollaborationImpl collaborationImpl = (CollaborationImpl) collaboration;
		ContextualizedComplexOperation op = aComplexOperation(collaborationImpl.getState());
		User user = createMock(User.class);
		replay(user);

		collaborationImpl.apply(op, user);
		Change c = collaboration.getLastChange();
		
		assertThat(c.getUser(), is(user));
	}
}
