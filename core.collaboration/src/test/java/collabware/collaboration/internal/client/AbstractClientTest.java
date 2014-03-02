package collabware.collaboration.internal.client;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.ClientParticipant;
import collabware.collaboration.client.Session;

public class AbstractClientTest {

	protected void initialize(Session s) {
		ComplexOperation initSequence = new ComplexOperationImpl("", Arrays.asList((PrimitiveOperation)NoOperation.NOP));
		List<ClientParticipant> participants = Collections.emptyList();
		((ClientImpl)s).initialize(initSequence, participants, null);
		
	}

	protected ClientEndpoint mockClientEndpointExpectingJoinFor(String collaborationId) {
		ClientEndpoint clientEndpoint = createNiceMock(ClientEndpoint.class);
		clientEndpoint.join(collaborationId);
		expectLastCall().once();
		replay(clientEndpoint);
		return clientEndpoint;
	}

}
