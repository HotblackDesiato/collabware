package collabware.collaboration.internal.client;

import static collabware.model.operations.OperationHelper.addNode;
import static collabware.model.operations.OperationHelper.complex;
import static collabware.model.operations.OperationHelper.init;
import static collabware.model.operations.OperationHelper.model;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientEndpointListener;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.Session;
import collabware.collaboration.client.replay.ReplayListener;
import collabware.model.internal.ops.transformation.GraphTransformationMatrix;
import collabware.transformer.internal.TransformationProviderImpl;

public class ClientReplayTest extends AbstractClientTest {
	
	private Session s;
	private String collaborationId = "blah";
	private ClientEndpoint clientEndpoint;

	@Before
	public void setup() {
		clientEndpoint = mockClientEndpointExpectingJoinFor(collaborationId);
		expectFetchReplaySequence(clientEndpoint);

		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
		transformationProvider.setPartiallyInclusiveOperationalTransformation(new GraphTransformationMatrix());
		s = new ClientImpl(clientEndpoint, transformationProvider);
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotStartReplayWhenNotInitialized() throws Exception {
		s.replay().enter();
	}

	@Test
	public void canStartReplayWhenInitialized() throws Exception {
		s.join(collaborationId);
		initialize(s);
		expectFetchReplaySequence(clientEndpoint);
		
		s.replay().enter();
		
		verify(clientEndpoint);
	}
	
	@Test
	public void startReplayAppliesReplayInitOperation() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		s.replay().enter();
		initReplay(s, init("n1"));
	
		assertThat(s.getDocument(), is(model("n1")));
	}

	@Test
	public void endReplayGoesBackToLastDocumentState() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		s.replay().enter();
		initReplay(s, init("n1"));
		s.replay().exit();
		
		assertThat(s.getDocument(), is(model("n1,n2,n1.ref->n2")));
	}

	@Test
	public void updatesAreQueuedWhileInReplay() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		s.replay().enter();
		initReplay(s, init("n1"));
		((ClientEndpointListener)s).update(complex(addNode("n3")), 0, 0);
		s.replay().exit();
		 
		assertThat(s.getDocument(), is(model("n1,n2,n3,n1.ref->n2")));
	}

	@Test
	public void updatesAreAppliedAfterReplayEnd() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		s.replay().enter();
		initReplay(s, init("n1"));
		s.replay().exit();
		((ClientEndpointListener)s).update(complex(addNode("n3")), 0, 0);
		
		assertThat(s.getDocument(), is(model("n1,n2,n3,n1.ref->n2")));
	}

	@Test
	public void listenerIsNotifiedWhenReplayStarted() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		ReplayListener l = createMock(ReplayListener.class);
		l.started();
		expectLastCall().once();
		replay(l);
		s.replay().addListener(l);
		s.replay().enter();
		initReplay(s, init("n1"));
		
		verify(l);
	}

	@Test
	public void listenerIsNotifiedWhenReplayEnded() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		ReplayListener l = createNiceMock(ReplayListener.class);
		l.ended();
		expectLastCall().once();
		replay(l);
		
		s.replay().addListener(l);
		
		s.replay().enter();
		initReplay(s, init("n1"));
		s.replay().exit();
		
		verify(l);
	}
	
	@Test
	public void listenerIsNotifiedWhenOperationIsReplayed() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		ReplayListener l = createNiceMock(ReplayListener.class);
		ComplexOperation op = complex(addNode("n2"));
		l.replayed(op);
		expectLastCall().once();
		replay(l);
		
		s.replay().addListener(l);
		
		s.replay().enter();
		initReplay(s, init("n1"), op);
		
		s.replay().getReplayControl().next();
		verify(l);
	}

	@Test
	public void afterLeavingReplayReplayControlsAreInactive() throws Exception {
		joinWithModel("n1,n2,n1.ref->n2");
		
		s.replay().enter();
		initReplay(s, init("n1"), complex(addNode("n2")), complex(addNode("n3")));
		s.replay().exit();
		
		assertFalse(s.replay().getReplayControl().canNext());
		assertFalse(s.replay().getReplayControl().canPrevious());
	}

	private void joinWithModel(String modeLiteral) {
		s.join(collaborationId);
		initialize(s, modeLiteral);
	}

	private void initReplay(Session s, ComplexOperation init) {
		((ClientEndpointListener)s).initReplay(init, Collections.<ComplexOperation>emptyList());
	}
	private void initReplay(Session s, ComplexOperation init, ComplexOperation ... ops) {
		((ClientEndpointListener)s).initReplay(init, Arrays.asList(ops));
	}

	private void initialize(Session session, String initModelLiteral) {
		List<ClientParticipant> participants = Collections.emptyList();
		((ClientEndpointListener)s).initialize(init(initModelLiteral), participants, null);
	}

	private void expectFetchReplaySequence(ClientEndpoint clientEndpoint) {
		reset(clientEndpoint);
		clientEndpoint.fetchReplaySequence();
		expectLastCall().once();
		replay(clientEndpoint);
	}
}
