package collabware.collaboration.server;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import collabware.collaboration.Client;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.NoSuchCollaborationException;
import collabware.collaboration.Participant;

public class ServerTest {

	private Server server;
	private CollaborationProvider collaborationProvider;
	private Collaboration mockCollaboration;
	private ServerEndpoint mockServerEndpoint;
	
	@Before
	public void setupCollaborationProvider() throws Exception {
		collaborationProvider = createNiceMock(CollaborationProvider.class);
		mockCollaboration = createNiceMock(Collaboration.class);
		Client mockClient = createNiceMock(Client.class);
		expect(mockCollaboration.join(anyObject(Participant.class))).andReturn(mockClient).once();
		expect(collaborationProvider.getCollaboration("someId")).andReturn(mockCollaboration).once();
		
		mockServerEndpoint = createNiceMock(ServerEndpoint.class);
		replay(collaborationProvider, mockCollaboration, mockClient, mockServerEndpoint);
		server = new Server(mockServerEndpoint, collaborationProvider);
	}
	
	@After
	public void verifyExpectations() {
		verify(collaborationProvider, mockCollaboration, mockServerEndpoint);
	}
	
	@Test
	public void join() throws NoSuchCollaborationException, ServerException {
		server.joinClient("someOtherId", createNiceMock(Participant.class), "someId");
		
	}
	
	@Test
	public void duplicateJoin() throws NoSuchCollaborationException, ServerException {
		
		server.joinClient("someOtherId", createNiceMock(Participant.class), "someId");
		
		try {
			server.joinClient("someOtherId", createNiceMock(Participant.class), "someId");
			fail("Expected ServerException");
		} catch (ServerException e) {
			assertTrue("Expected", true);
		}

	}
}
