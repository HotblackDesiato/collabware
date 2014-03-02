package collabware.web.cometd;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.json.JSONObject;
import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.collaboration.Participant;
import collabware.collaboration.client.ClientParticipant;
import collabware.model.internal.ModelProviderImpl;
import collabware.userManagement.User;
import collabware.web.client.cometd.InitMessage;
import collabware.web.client.cometd.UpdateMessage;
import collabware.web.cometd.messages.ApplyChangeMessage;
import collabware.web.cometd.messages.ChangeRequest;
import collabware.web.cometd.messages.JoinResponse;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.JsonProtocolConstants;

public class MessagesTest {
	@Test
	public void testServerJoinResponseToClientInitMessage() throws Exception {
		ComplexOperation initSequence = new ComplexOperationImpl("The init sequence", new ArrayList<PrimitiveOperation>());
		Participant localParticipant = createParticipant("0815", "http://myImage.de", "Alice");
		List<Participant> participants= Arrays.asList(createParticipant("0815", "http://myImage.de", "Alice"), createParticipant("42", "http://myImage.de", "Bob"));
		JoinResponse joinResponse = new JoinResponse(initSequence, localParticipant, participants);
		String serialized = joinResponse.getPayload().toString();
		
		InitMessage initMessage = new InitMessage(serialized, new ServerJsonProvider());
		
		assertThat(initMessage.getInitSequence(), is(equalTo(initSequence)));
		assertThatParticipantsEqual(initMessage.getLocalParticipant(), localParticipant);
		assertThatParticipantsEqual(initMessage.getParticipants(), participants);
	}

	private void assertThatParticipantsEqual(List<ClientParticipant> clientParticipants, List<Participant> serverParticipants) {
		assertThat(clientParticipants.size(), is(serverParticipants.size()));
		for (int i = 0; i < clientParticipants.size(); i++) {
			assertThatParticipantsEqual(clientParticipants.get(i), serverParticipants.get(i));
		}
	}

	private void assertThatParticipantsEqual(ClientParticipant clientParticipant,	Participant serverParticipant) {
		assertThat(clientParticipant.getDisplayName(), is(serverParticipant.getUser().getDisplayName()));
		assertThat(clientParticipant.getId(), is(serverParticipant.getUser().getUserName()));
		assertThat(clientParticipant.getImageUrl(), is("/collabware/images/"+serverParticipant.getUser().getUserName()+".png"));
	}

	private Participant createParticipant(String id, String imageUrl, String displayName) {
		Participant p = createMock(Participant.class);
		User user = createMock(User.class);
		expect(p.getUser()).andStubReturn(user);
		
		expect(user.getUserName()).andStubReturn(id);
		expect(user.getDisplayName()).andStubReturn(displayName);
		replay(user, p);
		return p;
	}

	@Test
	public void testApplyChangeToUpdateMessage() throws Exception {
		ApplyChangeMessage applyChange = new ApplyChangeMessage(12, 18, new ComplexOperationImpl("Some change", new ArrayList<PrimitiveOperation>()));
		String serialized = applyChange.getPayload().toString();
		
		UpdateMessage updateMessage = new UpdateMessage(serialized, new ServerJsonProvider());
		
		assertThat(updateMessage.getClientSequenceNumber(), is(applyChange.getClientSequenceNumber()));
		assertThat(updateMessage.getServerSequenceNumber(), is(applyChange.getServerSequenceNumber()));
		assertThat(updateMessage.getChange(), is(applyChange.getChange()));
	}
	
	@Test
	public void updateMessageToApplyChange() throws Exception {
		ComplexOperation complexOperation = new ComplexOperationImpl("Some change", new ArrayList<PrimitiveOperation>());
		int clientSequenceNumber = 21;
		int serverSequenceNumber = 13;
		
		UpdateMessage updateMessage = new UpdateMessage(complexOperation,clientSequenceNumber, serverSequenceNumber);
		String serialized = updateMessage.toString(new ServerJsonProvider());

		ChangeRequest changeRequest = new ChangeRequest(mockRemote(), createMessage(serialized), new ModelProviderImpl());
		
		assertThat(changeRequest.getOperation(), is(complexOperation));
		assertThat(changeRequest.getClientSequenceNumber(), is(clientSequenceNumber));
		assertThat(changeRequest.getServerSequenceNumber(), is(serverSequenceNumber));
	}

	private Mutable createMessage(String serialized) throws Exception {
		Mutable m = createMock(Mutable.class);
		JSONObject json = new JSONObject();
		json.put(JsonProtocolConstants.PAYLOAD,serialized);
		expect(m.getJSON()).andReturn(json.toString());
		ChannelId value = new ChannelId("/1/2/3456");
		expect(m.getChannelId()).andReturn(value);
		replay(m);
		return m;
	}

	private ServerSession mockRemote() {
		ServerSession mock = createNiceMock(ServerSession.class);
		expect(mock.getId()).andStubReturn("1234");
		replay(mock);
		return mock;
	}
	
}

