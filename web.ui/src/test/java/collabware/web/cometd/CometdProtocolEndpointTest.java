package collabware.web.cometd;

import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.ServerMessageImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.Participant;
import collabware.model.ModifyableModel;
import collabware.userManagement.User;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.OperationJsonizer;

public class CometdProtocolEndpointTest extends AbstractCometdProtocolEndpointTest{

	@Test
	public void joinNonexistingCollaboration() throws Exception {
		ServerSession remote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(false));
				assertThat(message.getString("error"), is("No such collaboration."));
			}
		});		
		
		server.join(remote, joinMessageForCollaboration("0815"));
	
		verify(remote);
	}

	@Test
	public void joinExistingCollaboration() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1,n2,n1.foo=42,n1.bar->n2");
		final Collaboration collaboration = createCollaborationWithModel(model);
		
		ServerSession remote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
				assertThat(message.getJSONObject("initSequence"), reconstructs(model));
				assertEquals(message.getJSONObject("localParticipant"), loggedInUser);
				assertEquals(message.getJSONArray("participants"), collaboration.getParticipants());
			}

		});
		
		server.join(remote, joinMessageForCollaboration(collaboration));
		
		verify(remote);
	}

	private void assertEquals(JSONArray jsonAllParticipants, List<Participant> participants) throws JSONException {
		assertThat(jsonAllParticipants.length(), is(participants.size()));
		for (int i = 0; i < participants.size(); i++) {
			assertEquals(jsonAllParticipants.getJSONObject(i), participants.get(i).getUser());
		}
		
	}
	private void assertEquals(JSONObject localParticipant, User user) throws JSONException {
		assertThat(localParticipant.getString("displayName"), is(user.getDisplayName()));
		assertThat(localParticipant.getString("id"), is(user.getUserName()));
		assertThat(localParticipant.getString("imageUrl"), is("/collabware/images/"+user.getUserName()+".png"));
	}

	@Test
	public void apply() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		ServerSession remote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}
		, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/acknowledge"));
				assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
				assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
			}}
		);
		
		server.join(remote, joinMessageForCollaboration(col));
		
		server.applyClientChange(remote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2"));
		verify(remote);
	}
	@Test
	@Ignore
	public void applyWithoutJoin() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		ServerSession remote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/acknowledge"));
				assertThat(message.getBoolean("successful"), is(false));
				assertThat(message.getString("error"), is("Join first."));
			}
		});
		
		server.applyClientChange(remote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1"));
		verify(remote);
	}

	@Test
	public void multipleApply() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		ServerSession remote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(2));
				}
			});
		
		server.join(remote, joinMessageForCollaboration(col));
		
		server.applyClientChange(remote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		server.applyClientChange(remote, messageWithOperation(col.getId(), complex( addNode("n3") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2,n3"));
		verify(remote);
	}

	@Test
	public void applyAndBroadcast() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				}
			});

		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/update"));
				assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
				assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
				assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( addNode("n2") )).toString()));
			}});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2"));
		verify(activeRemote, passiveRemote);
	}

	@Test
	public void multipleApplyAndBroadcast() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(2));
			}});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/update"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
					assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( addNode("n2") )).toString()));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/update"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(1));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
					assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( addNode("n3") )).toString()));
			}});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( addNode("n3") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2,n3"));
		verify(activeRemote, passiveRemote);
	}
	
	@Test
	@Ignore
	public void applyConflictingOperation() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				}
			}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/update"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
					assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( id())).toString()));
			}});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/update"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
					assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( updAttr("n1", "attr", null, "42") )).toString()));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(1));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
				}
			});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( updAttr("n1", "attr", null, "42") ) ));
		server.applyClientChange(passiveRemote, messageWithOperation(col.getId(), complex( updAttr("n1", "attr", null, "foobar") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n1.attr='42'"));
		verify(activeRemote, passiveRemote);
	}
	@Test
	public void applyConcurrentOperations() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
			}}, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/update"));
				assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
				assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( updAttr("n1", "foo", null, "bar") )).toString()));
			}
		});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/update"));
				assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
				assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(0));
				assertThat(message.getJSONObject(OPERATION).toString(), is(serialize(complex( updAttr("n1", "attr", null, "42") )).toString()));
			}}, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/acknowledge"));
				assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(1));
				assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
			}
		});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( updAttr("n1", "attr", null, "42") ) ));
		server.applyClientChange(passiveRemote, messageWithOperation(col.getId(), complex( updAttr("n1", "foo", null, "bar") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n1.attr='42',n1.foo='bar'"));
		verify(activeRemote, passiveRemote);
	}
	


	protected JSONObject serialize(ComplexOperation complex) {
		OperationJsonizer<JSONObject,JSONArray> jsonizer = new OperationJsonizer<JSONObject,JSONArray>(new ServerJsonProvider());
		return jsonizer.jsonize(complex);
	}

	@Test
	public void leaveCollaboration() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				}
			});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.leave(passiveRemote, leaveMessageForCollaboration(col));
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2"));
		verify(activeRemote, passiveRemote);
	}
	
	@Test
	public void leaveCollaborationAfterTimeout() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				}
			});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}});
		
		server.join(activeRemote, joinMessageForCollaboration(col));
		server.join(passiveRemote, joinMessageForCollaboration(col));
		
		server.removed(passiveRemote, true);
		
		server.applyClientChange(activeRemote, messageWithOperation(col.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col.getId(), modelFromLiteral("n1,n2"));
		verify(activeRemote, passiveRemote);
	}
	
	@Ignore
	@Test
	public void joinDifferentCollaborationsWithSameRemote() throws Exception {
		final ModifyableModel model = modelFromLiteral("n1");
		Collaboration col1 = createCollaborationWithModel(model);
		Collaboration col2 = createCollaborationWithModel(model);
		
		ServerSession activeRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
				public void expect(String channel, JSONObject message) throws Exception {
					assertThat(channel, is("/acknowledge"));
					assertThat(message.getInt(SERVER_SEQUENCE_NUMBER), is(0));
					assertThat(message.getInt(CLIENT_SEQUENCE_NUMBER), is(1));
				}
			});
		
		ServerSession passiveRemote = mockRemoteSession(new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}}, new ExpectedMessage() {
			public void expect(String channel, JSONObject message) throws Exception {
				assertThat(channel, is("/initialize"));
				assertThat(message.getBoolean("successful"), is(true));
			}});
		
		server.join(activeRemote, joinMessageForCollaboration(col1));
		server.join(passiveRemote, joinMessageForCollaboration(col1));
		server.join(passiveRemote, joinMessageForCollaboration(col2));
		
		server.applyClientChange(activeRemote, messageWithOperation(col1.getId(), complex( addNode("n2") ) ));
		
		assertCollaborationHasModel(col1.getId(), modelFromLiteral("n1,n2"));
		verify(activeRemote, passiveRemote);
	}

	private Mutable leaveMessageForCollaboration(Collaboration col) {
		Mutable message = new ServerMessageImpl();
		message.setChannel("/service/leave/" + col.getId());
		return message;
	}
}
