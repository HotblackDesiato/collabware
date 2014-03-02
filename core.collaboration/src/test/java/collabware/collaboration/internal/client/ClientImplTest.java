package collabware.collaboration.internal.client;

import static collabware.model.operations.OperationHelper.addNode;
import static collabware.model.operations.OperationHelper.complex;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.document.change.ChangeListener;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.Command;
import collabware.collaboration.client.Session;
import collabware.collaboration.client.SessionListener;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.transformer.internal.TransformationProviderImpl;

public class ClientImplTest  extends AbstractClientTest {

	
	private Session s;
	private String collaborationId = "blah";
	private ClientEndpoint clientEndpoint;

	@Before
	public void setup() {
		clientEndpoint = mockClientEndpointExpectingJoinFor(collaborationId);
		
		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
		transformationProvider.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		s = new ClientImpl(clientEndpoint, transformationProvider);
	}
	
	@Test
	public void join() {
		s.join(collaborationId);
		
		verify(clientEndpoint);
	}
	
	@Test
	public void afterSuccessfulJoinSessionListenerIsNotified() {
		SessionListener l = sessionListenerExpectingJoined();
		s.addSessionListener(l);

		s.join(collaborationId);
		
		initialize(s);
		verify(l);
	}

	@Test(expected=IllegalStateException.class)
	public void canOnlyBeInitializedOnce() {
		s.join(collaborationId);
		initialize(s);

		initialize(s);
	}
	
	private SessionListener sessionListenerExpectingJoined() {
		SessionListener l = createMock(SessionListener.class);
		l.joined();
		expectLastCall().once();
		replay(l);
		return l;
	}

	
	@Test(expected= IllegalStateException.class)
	public void canOnlyJoinOnce() throws Exception {
		s.join(collaborationId);
		
		s.join("someOtherCollaboration");
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotApplyChangeWhenDisconnected() throws Exception {
		s.applyChange(someChange());
	}

	@Test(expected=IllegalStateException.class)
	public void cannotApplyChangeWhenJoining() throws Exception {
		s.join(collaborationId);

		s.applyChange(someChange());
	}
	
	@Test
	public void canOnlyApplyChangeWhenJoined() throws Exception {
		s.join(collaborationId);
		initialize(s);
		
		s.applyChange(someChange());
	}

	
	@Test
	public void emptyChangesAreIgnored() throws Exception {
		s.join(collaborationId);
		initialize(s);
		
		s.applyChange(emptyChange());
	}
	
	@Test
	public void updatesTriggerModelChange() throws Exception {
		s.join(collaborationId);
		ChangeListener listener = mockListenerExpectingComplexChangeEnded();
		initialize(s);
		s.getDocument().addChangeListener(listener);
		
		((ClientEndpointListener)s).update(complex(addNode("n3")), 0, 0);
		
		verify(listener);
	}

	private ChangeListener mockListenerExpectingComplexChangeEnded() {
		ChangeListener listener = createNiceMock(ChangeListener.class);
		listener.notifyChange(isA(ComplexChangeEnded.class));
		expectLastCall().once();
		replay(listener);
		return listener;
	}
	
	private Command emptyChange() {
		return new Command() {
			@Override
			public String getDescription() {return "";}

			@Override
			public void apply(ModifyableDocument doc) {}
		};
	}

	private Command someChange() {
		return new Command() {
			
			@Override
			public String getDescription() {
				return "some changes";
			}
			
			@Override
			public void apply(ModifyableDocument doc) {
				ModifyableGraph graph = ((ModifyableModel)doc).getGraph();
				graph.addNode("n1");
			}
		};
	}


}
