package collabware.web.cometd;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.cometd.bayeux.server.BayeuxContext;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerSession.RemoveListener;
import org.cometd.server.ServerMessageImpl;
import org.easymock.Capture;
import org.easymock.IAnswer;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;
import collabware.collaboration.internal.CollaborationProviderImpl;
import collabware.collaboration.internal.ParticipantImpl;
import collabware.model.Model;
import collabware.model.ModelProvider;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.transformer.internal.TransformationProviderImpl;
import collabware.userManagement.User;
import collabware.userManagement.UserDetails;
import collabware.userManagement.internal.UserImpl;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.OperationDejsonizer;
import collabware.web.shared.OperationJsonizer;

public class AbstractCometdProtocolEndpointTest {

	public interface ExpectedMessage {
		void expect(String channel, JSONObject message) throws Exception;
	}

	public class InitSequenceMatcher extends BaseMatcher<Object> {
		private final Model model;
	
		public InitSequenceMatcher(Model m) {
			this.model = m;
		}
		
		public boolean matches(Object jsonizedInitSequence) {
			if (jsonizedInitSequence instanceof JSONObject) {
				try {
					Operation op = dejsonizer.dejsonize((JSONObject) jsonizedInitSequence);
					ModifyableDocument model = modelProvider.createDocument("");
					op.apply(model);
					return model.equals(this.model);
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
		public void describeTo(Description arg0) {}
	}

	protected static final String OPERATION = "o";
	protected static final String CLIENT_SEQUENCE_NUMBER = "c";
	protected static final String SERVER_SEQUENCE_NUMBER = "s";

	protected final CometdProtocolEndpoint server = new CometdProtocolEndpoint();
	protected final ModelProvider modelProvider = new ModelProviderImpl();
	protected OperationDejsonizer dejsonizer;
	private OperationJsonizer jsonizer = new OperationJsonizer(new ServerJsonProvider());
	protected UserImpl loggedInUser;

	@Before
	public void setup() {
		server.operationsProvider = new ModelProviderImpl();
		server.collaborationProvider = new CollaborationProviderImpl();
		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
		transformationProvider.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		((CollaborationProviderImpl) server.collaborationProvider).setTransformationProvider(transformationProvider);
		server.securityContext = mockSecurityContext();
		server.bayeux = createBayeuxService();
		server.serverSession = createNiceMock(ServerSession.class);
		replay(server.serverSession);
		dejsonizer = new OperationDejsonizer(server.operationsProvider, new ServerJsonProvider());
		server.startServer();
	}

	private SecurityContext mockSecurityContext() {
		SecurityContext mockSecurityContext = createMock(SecurityContext.class);
		Authentication authentication = createMock(Authentication.class);
		loggedInUser = new UserImpl(new UserDetails("thomas.hettel", "1234", "Thomas Hettel"));
		expect(authentication.getPrincipal()).andReturn(loggedInUser).anyTimes();
		expect(mockSecurityContext.getAuthentication()).andReturn(authentication).anyTimes();
		replay(authentication, mockSecurityContext);
		return mockSecurityContext;
	}
	private HashMap<String, ServerSession> sessions = new HashMap<String, ServerSession>();

	private BayeuxServer createBayeuxService() {
		BayeuxServer bayeux = createMock(BayeuxServer.class);
		BayeuxContext context = createMock(BayeuxContext.class);
		expect(context.getHttpSessionAttribute(HttpSessionAttributes.COLLABWARE_AUTHENTICATED_USER)).andStubReturn(createMockUser());
		expect(bayeux.getContext()).andStubReturn(context);
		final Capture<String> sessionId = new Capture<String>();
		expect(bayeux.getSession(capture(sessionId))).andStubAnswer(new IAnswer< ServerSession >() {

			@Override
			public  ServerSession answer() throws Throwable {
				return sessions.get(sessionId.getValue());
			}});
		
		replay(context, bayeux);
		return bayeux;
	}

	private User createMockUser() {
		User user = createMock(User.class);
		expect(user.getDisplayName()).andStubReturn("foobar");
		expect(user.getUserName()).andStubReturn("foobar");
		
		replay(user);
		return user;
	}

	protected Matcher<Object> reconstructs(Model m) {
		return new InitSequenceMatcher(m);
	}

	protected void assertCollaborationHasModel(String string, Model modelFromLiteral)
			throws NoSuchCollaborationException {
				assertEquals(server.collaborationProvider.getCollaboration(string).getDocument(), modelFromLiteral);
			}

	protected Mutable messageWithOperation(String collaborationId, ComplexOperation complex)
			throws JSONException {
				Mutable message = new ServerMessageImpl();
				message.setChannel("/service/clientChange/" + collaborationId);
				message.setData(payload(0, 0, complex));
				return message;
			}

	private JSONObject payload(int serverSequenceNumber, int clientSequenceNumber, ComplexOperation complex)
			throws JSONException {
				JSONObject paylaod = new JSONObject();
				paylaod.put(SERVER_SEQUENCE_NUMBER, serverSequenceNumber);
				paylaod.put(CLIENT_SEQUENCE_NUMBER, clientSequenceNumber);
				JSONArray ops = new JSONArray();
				ops.put(0, jsonizer.jsonize(complex));
				paylaod.put(OPERATION, ops);
				return paylaod;
			}

	protected ComplexOperation complex(PrimitiveOperation ...ops) {
		return new ComplexOperationImpl("", Arrays.asList(ops));
	}

	protected PrimitiveOperation addNode(String nodeId) {
		return new AddNodeOperation(nodeId);
	}
	
	protected PrimitiveOperation updAttr(String nodeId, String attr, Object oldValue, String newValue) {
		return new SetAttributeOperation(nodeId, attr, oldValue, newValue);
	}
	protected PrimitiveOperation id() {
		return NoOperation.NOP;
	}

	protected ServerSession mockRemoteSession(final ExpectedMessage ...expectedMessages) {
		ServerSession remote = createMock(ServerSession.class);
		String id = UUID.randomUUID().toString();
		expect(remote.getId()).andStubReturn(id);
		for (final ExpectedMessage messageReceiver: expectedMessages) {
			final Capture<JSONObject> payload = new Capture<JSONObject>();
			final Capture<String> channel = new Capture<String>();
			remote.deliver(anyObject(ServerSession.class), capture(channel), capture(payload), anyObject(String.class));
			expectLastCall().andAnswer(new IAnswer<Object>() {
				public Object answer() throws Throwable {
					messageReceiver.expect(channel.getValue(), payload.getValue());
					return null;
				}
			}).once();
		}
		
		remote.addListener(anyObject(RemoveListener.class));
		expectLastCall().once();
		
		replay(remote);
		sessions.put(id, remote);
		return remote;
	}

	protected Mutable joinMessageForCollaboration(Collaboration col) {
		Mutable message = new ServerMessageImpl();
		message.setChannel("/service/join/" + col.getId());
		return message;
	}
	
	protected Mutable joinMessageForCollaboration(String id) {
		Mutable message = new ServerMessageImpl();
		message.setChannel("/service/join/" + id);
		return message;
	}

	protected Collaboration createCollaborationWithModel(ModifyableModel model) {
		CollaborationProviderImpl colProvider = (CollaborationProviderImpl) server.collaborationProvider;
		
		ModelProvider mockModelProvider = createMock(ModelProvider.class);
		expect(mockModelProvider.createDocument(anyObject(String.class))).andStubReturn(model);
		replay(mockModelProvider);
	
		colProvider.setModelProvider(mockModelProvider);
	
		Participant owner = new ParticipantImpl(createMockUser());
		CollaborationDetails details = new CollaborationDetails("", "");
				
		return colProvider.createCollaboration(details , owner);
	}

	protected ModifyableModel modelFromLiteral(String literal) {
		return modelProvider.createModelFromLiteral("someType", literal);
	}

}
