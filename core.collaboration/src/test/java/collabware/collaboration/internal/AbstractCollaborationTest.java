package collabware.collaboration.internal;

import org.easymock.EasyMock;
import org.junit.Before;

import collabware.api.document.ModifyableDocument;
import collabware.collaboration.Client;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationDetails;
import collabware.collaboration.Participant;

public class AbstractCollaborationTest {

	protected Collaboration collaboration;

	public AbstractCollaborationTest() {
		super();
	}

	@Before
	public void setup() throws Exception {
		collaboration = createCollaboration();		
	}

	protected Participant mockParticipant() {
		return EasyMock.createMock(Participant.class);
	}

	protected Client mockClient() {
		return EasyMock.createMock(Client.class);
	}

	private CollaborationImpl createCollaboration() throws Exception {
		CollaborationDetails details = new CollaborationDetails("someName", "someType");
		return new CollaborationImpl(mockModel(), new ParticipantImpl(null), details, new NoTransformer());
	}

	private ModifyableDocument mockModel() {
		return EasyMock.createMock(ModifyableDocument.class);
	}

}