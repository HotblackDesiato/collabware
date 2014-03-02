package collabware.collaboration;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.document.change.ChangeListener;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.collaboration.client.Session;
import collabware.collaboration.client.Command;
import collabware.collaboration.internal.CollaborationProviderImpl;
import collabware.collaboration.internal.ParticipantImpl;
import collabware.collaboration.internal.client.ClientImpl;
import collabware.collaboration.network.NetworkImpl;
import collabware.collaboration.server.Server;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.transformer.internal.TransformationProviderImpl;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
import collabware.userManagement.internal.UserImpl;

public class ClientServerIntegrationTest {

	private TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
	private NetworkImpl network = new NetworkImpl();
	private Session c1;
	private Session c2;
	private Session c3;
	private CollaborationProviderImpl collaborationProvider;
	private Server s;
	
	@Before
	public void setup () {
		transformationProvider.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		collaborationProvider = createCollaborationProvider();
		
		c1 = new ClientImpl(network.createClientConnector(), transformationProvider);
		c2 = new ClientImpl(network.createClientConnector(), transformationProvider);
		c3 = new ClientImpl(network.createClientConnector(), transformationProvider);
		
		s = new Server(network.getServerConnector(), collaborationProvider);
	}
	
	private CollaborationProviderImpl createCollaborationProvider() {
		ModelProvider modelProvider = new ModelProviderImpl();
		CollaborationProviderImpl collaborationProvider = new CollaborationProviderImpl();
		collaborationProvider.setModelProvider(modelProvider);
		collaborationProvider.setTransformationProvider(transformationProvider);
		return collaborationProvider;
	}

	@Test
	public void testInitialization() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());

		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(s.getCollaboration(collaboration.getId()).getDocument()));
		assertThat(c1.getLocalParticipant(), is(not(nullValue())));
	}

	@Test
	public void testDisconnect() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());
		
		network.dispatchNow();
		
		c1.disconnect();
		
		assertThat(c1.getDocument().isEmpty(), is(true));
		assertThat(c1.getParticipants().isEmpty(), is(true));
		assertThat(c1.getLocalParticipant(), is(nullValue()));
	}

	@Test
	public void testClientApplysChange() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());
		network.dispatchNow();
		
		c1.applyChange(addNode("n2"));
		
		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(s.getCollaboration(collaboration.getId()).getDocument()));
	}

	@Test
	public void testChangesOfOneClientAreForwaredToOtherClients() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());
		c2.join(collaboration.getId());
		network.dispatchNow();
		ChangeListener listener = mockChangeListener();
		c2.getDocument().addChangeListener(listener);

		c1.applyChange(addNode("n2"));
		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
		verify(listener);
	}

	private ChangeListener mockChangeListener() {
		ChangeListener listener = createNiceMock(ChangeListener.class);
		listener.notifyChange(isA(ComplexChangeEnded.class));
		expectLastCall().once();
		listener.notifyChange(isA(collabware.api.document.change.Change.class));
		expectLastCall().atLeastOnce();
		replay(listener);
		return listener;
	}
	
	@Test
	public void testBothClientMakeChanges() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());
		c2.join(collaboration.getId());
		network.dispatchNow();
		c1.applyChange(addNode("n2"));
		network.dispatchNow();
		c2.applyChange(addNode("n3"));
		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}

	@Test
	public void testBothClientsMakeConcurrentChanges() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n1.attr='blah'");
		c1.join(collaboration.getId());
		c2.join(collaboration.getId());
		network.dispatchNow();
		c1.applyChange(addNode("n2"));
		c2.applyChange(addNode("n2"));
		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}

	@Test
	public void testBothClientMakeConcurrentChanges2() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1,n2,n3,n4,n1.ref[0]->n2");

		c1.join(collaboration.getId());						c2.join(collaboration.getId());
											network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n2"));
		c1.applyChange(addRef("n1","ref", 2, "n3"));		c2.applyChange(addRef("n1","ref", 0, "n4"));
											network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}

	@Test
	public void testClientsMakeConflictingChanges() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n1.ref[0]->n1");
		
		c1.join(collaboration.getId());						c2.join(collaboration.getId());
											network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n2"));		c2.applyChange(addRef("n1","ref", 0, "n2"));
											network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}
	
	@Test
	public void testClientsMakeConflictingChanges_2() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n3, n1.ref[0]->n1");
		
		c1.join(collaboration.getId());						c2.join(collaboration.getId());
											network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n2"));		c2.applyChange(addRef("n1","ref", 0, "n2"));
		c1.applyChange(addRef("n1","ref", 2, "n3"));		
											network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}
	
	@Test
	public void testClientsMakeConflictingChanges_3() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n3, n1.ref[0]->n1");
		
		c1.join(collaboration.getId());						c2.join(collaboration.getId());
											network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n2"));		c2.applyChange(addRef("n1","ref", 0, "n2"));
											network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n3"));		c2.applyChange(addRef("n1","ref", 1, "n3"));		
											network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
	}

	@Test
	public void testThreeClientsMakeConflictingChanges() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n3, n1.ref[0]->n1, n1.ref[1]->n2");
		
		c1.join(collaboration.getId());					c2.join(collaboration.getId());					c3.join(collaboration.getId());
		network.dispatchNow();
		
		c1.applyChange(addRef("n1","ref", 1, "n3"));	c2.applyChange(addRef("n1","ref", 0, "n3"));	c3.applyChange(addRef("n1","ref", 2, "n3"));
		network.dispatchNow();
		
		assertThat(c1.getDocument(), equalTo(c2.getDocument()));
		assertThat(c1.getDocument(), equalTo(c3.getDocument()));
	}

	@Test
	public void givenCollaborationWithSomeParticiapntswhenJoinedThenClientHasListOfParticipants() throws NoSuchCollaborationException {
		Collaboration collaboration = givenCollaborationWithParticipants();

		c1.join(collaboration.getId());
		network.dispatchNow();
		
		assertThatParticipantsMatch(c1.getParticipants(), collaboration.getParticipants());
	}
	@Test
	public void givenCollaborationWhenParticipantIsAddedThenClientHasHimInListOfParticipants() throws NoSuchCollaborationException {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n3, n1.ref[0]->n1, n1.ref[1]->n2");
		
		c1.join(collaboration.getId());
		network.dispatchNow();
		collaboration.addParticipant(new ParticipantImpl(new UserImpl(new UserDetails("Json.Bourne", "1234", "Json Bourne"))));
		network.dispatchNow();
		
		assertThatParticipantsMatch(c1.getParticipants(), collaboration.getParticipants());
	}

	private void assertThatParticipantsMatch(List<collabware.collaboration.client.ClientParticipant> participants, List<Participant> participants2) {
		assertThat("Sizes are equals", participants.size(), is(participants2.size()));
		for (int i = 0; i < participants.size(); i++) {
			assertThatParticipantsMatch(participants.get(i), participants2.get(i));
		}
	}

	private void assertThatParticipantsMatch(collabware.collaboration.client.ClientParticipant participant, Participant participant2) {
		User user = participant2.getUser();
		assertThat("participant ids match", participant.getId(), equalTo(user.getUserName()));
		assertThat("display names match", participant.getDisplayName(), equalTo(user.getDisplayName()));
	}

	private Collaboration givenCollaborationWithParticipants() {
		Collaboration collaboration = givenACollaboration("model", "n1, n2, n3, n1.ref[0]->n1, n1.ref[1]->n2");
		collaboration.addParticipant(new ParticipantImpl(new UserImpl(new UserDetails("Json.Bourne", "1234", "Json Bourne"))));
		collaboration.addParticipant(new ParticipantImpl(new UserImpl(new UserDetails("James.Bond", "007", "James Bond"))));
		return collaboration;
	}
	
	private Command addRef(final String nodeId, final String ref, final int i, final String targetId) {
		return new Command() {
					
			@Override
			public String getDescription() {
				return "";
			}
			
			public void apply(ModifyableGraph graph) {
				ModifyableNode source = graph.getNode(nodeId);
				ModifyableNode target = graph.getNode(targetId);
				source.getNaryReferences().add(ref, i, target);
			}

			@Override
			public void apply(ModifyableDocument document) {
				apply(((ModifyableModel)document).getGraph());
				
			}
		};
	}

	private Command addNode(final String string) {
		return new Command() {
			
			@Override
			public String getDescription() {
				return "";
			}
			
			@Override
			public void apply(ModifyableDocument document) {
				apply(((ModifyableModel)document).getGraph());
				
			}

			public void apply(ModifyableGraph graph) {
				graph.addNode(string);
			}
		};
	}


	private Collaboration givenACollaboration(String collaborationId, String modelListeral) {
		UserDetails userDetails = new UserDetails("TheOwner", "234", "The Owner");
		ModelProvider modelProviderMock = createMock(ModelProvider.class);
		
		ModelProviderImpl modelProvider = new ModelProviderImpl();
		ModifyableModel model = modelProvider.createModelFromLiteral("", modelListeral);
		expect(modelProviderMock.createDocument(anyObject(String.class))).andStubReturn(model);
		replay(modelProviderMock);
		collaborationProvider.setModelProvider(modelProviderMock);
		
		User user = new UserImpl(userDetails);
		Participant owner= new ParticipantImpl(user);
		Collaboration collaboration = collaborationProvider.createCollaboration(new CollaborationDetails("", ""), owner);
		return collaboration;
	}

}
