package collabware.collaboration.internal;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.Change;
import collabware.collaboration.ChangeListener;
import collabware.collaboration.Client;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;
import collabware.model.ModelProvider;
import collabware.transformer.TransformationProvider;
import collabware.userManagement.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/bundle-context.xml"})
public class CollaborationProviderTest {

	private final static String SOME_USER_NAME = "someUserName";
	private CollaborationProviderImpl collaborationProvider;
	private Participant owner;
	
	@Autowired
	private ModelProvider modelProvider;

	@Autowired
	private TransformationProvider transformationProvider;
	
	@Before
	public void setUp() {
		this.collaborationProvider = createCollaborationProvider();
		this.owner = new ParticipantImpl(createMockUserWith("Justin Case"));
	}

	@Test
	public void createCollaboration() throws Exception {
		
		CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
		Collaboration col = collaborationProvider.createCollaboration(details, owner);
		
		assertNotNull(col.getId());
		assertNotNull(col.getDocument());
		assertEquals("My Collaboration", col.getName());
		assertEquals(owner, col.getOwner());
		assertTrue(collaborationProvider.hasCollaboration(col.getId()));
	}

	@Test
	public void reCreateCollaboration() throws Exception {
		CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
		Collaboration collaboration1 = collaborationProvider.createCollaboration(details, owner);
		Collaboration collaboration2 = collaborationProvider.createCollaboration(details, owner);
		
		assertFalse(collaboration1.getId().equals(collaboration2.getId()));
		
	}
	
	@Test(expected=NoSuchCollaborationException.class)
	public void getNonexistingCollaboration() throws Exception {
		CollaborationProvider colProvider = new CollaborationProviderImpl();
		
		colProvider.getCollaboration("0815");
	}

	@Test
	public void getExistingCollaboration() throws Exception {
		CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
		Collaboration createdCol = collaborationProvider.createCollaboration(details, owner);
		
		Collaboration getCol = collaborationProvider.getCollaboration(createdCol.getId());
		
		assertEquals(createdCol, getCol);
	}
	
	@Test
	public void getAllCollaborations() throws Exception {
		CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
		Collaboration collaboration1 = collaborationProvider.createCollaboration(details, owner);
		Collaboration collaboration2 = collaborationProvider.createCollaboration(details, owner);
		Collaboration collaboration3 = collaborationProvider.createCollaboration(details, owner);
		
		Collection<Collaboration> allCollaborations = collaborationProvider.getAllCollaborations();
		
		assertTrue(allCollaborations.contains(collaboration1));
		assertTrue(allCollaborations.contains(collaboration2));
		assertTrue(allCollaborations.contains(collaboration3));
		assertEquals(3, allCollaborations.size());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void allCollaborationsUnmodifyable() throws Exception {
		CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
		Collaboration collaboration1 = collaborationProvider.createCollaboration(details, owner);
		Collection<Collaboration> allCollaborations = collaborationProvider.getAllCollaborations();
		
		allCollaborations.remove(collaboration1);
	}
	
	@Test
	public void getParticipantForUser() throws Exception {
		User user = createMockUserWith(SOME_USER_NAME);
		Participant participant = collaborationProvider.getParticipant(user);
		assertEquals(user, participant.getUser());
	}

	@Test
	public void reGetParticipantForUser() throws Exception {
		User user = createMockUserWith(SOME_USER_NAME);
		CollaborationProvider colProvider = new CollaborationProviderImpl();
		Participant participant1 = colProvider.getParticipant(user);
		Participant participant2 = colProvider.getParticipant(user);
		assertEquals(participant1, participant2);
	}

	@Test
	public void getOwnedCollaborationsForUser() throws Exception {
		Participant participant = collaborationProvider.getParticipant(createMockUserWith(SOME_USER_NAME));
		
		Set<Collaboration> createCollaborations = createSomeCollaborations(3, participant, collaborationProvider);
		Set<Collaboration> ownedCollaborations = participant.getOwnedCollaborations();
		
		assertEquals(3, ownedCollaborations.size());
		assertTrue(ownedCollaborations.containsAll(createCollaborations));
		assertCannotBeManipulated(ownedCollaborations);
	}
	
	@Test
	public void addEventListener() throws Exception {
		CollaborationDetails details = new CollaborationDetails("", "");
		Collaboration collaboration = collaborationProvider.createCollaboration(details, owner);
		Participant participant = owner;
		Client join = collaboration.join(participant);
		String description = "Some Complex Operation";

		Change change = new ChangeImpl(description, collaboration, participant.getUser());
		ChangeListener listener = createChangeListenerExpectingChange(change);
		ComplexOperation op = createSomeComplexOperation(description);
		
		collaborationProvider.addChangeListener(listener);
		join.applyChangeToCollaboration(0, op);
		
		verify(listener);
	}

	private ChangeListener createChangeListenerExpectingChange(Change change) {
		ChangeListener listener = createMock(ChangeListener.class);
		listener.changeApplied(eq(change));
		expectLastCall();
		replay(listener);
		return listener;
	}

	private ComplexOperation createSomeComplexOperation(String description)	throws OperationApplicationException {
		List<PrimitiveOperation> emptyList = new ArrayList<PrimitiveOperation>();
		PrimitiveOperation o = createMock(PrimitiveOperation.class);
		o.apply((ModifyableDocument) anyObject());
		expectLastCall();
		replay(o);
		emptyList.add(o);
		ComplexOperation op = new ComplexOperationImpl(description, emptyList);
		return op;
	}

	private CollaborationProviderImpl createCollaborationProvider() {
		CollaborationProviderImpl collaborationProvider = new CollaborationProviderImpl();	
		collaborationProvider.setModelProvider(modelProvider);
		collaborationProvider.setTransformationProvider(transformationProvider);
		collaborationProvider.startup();
		return collaborationProvider;
	}

	private User createMockUserWith(String userName) {
		User user = createMock(User.class);
		expect(user.getUserName()).andReturn(userName).anyTimes();
		replay(user);
		return user;
	}

	@SuppressWarnings("rawtypes")
	private void assertCannotBeManipulated(Collection collection) {
		try {
			collection.clear();
		} catch (UnsupportedOperationException expected) {
			return;
		}
		fail("collection can be modified.");
	}

	private Set<Collaboration> createSomeCollaborations(int numberOfCollaborations, Participant participant, CollaborationProvider colProvider) {
		HashSet<Collaboration> createdCollaborations = new HashSet<Collaboration>();
		for (int i = 0; i < numberOfCollaborations; i++) {
			CollaborationDetails details = new CollaborationDetails("My Collaboration", "myType");
			createdCollaborations.add(colProvider.createCollaboration(details, participant));
		}
		return createdCollaborations;
	}
}
