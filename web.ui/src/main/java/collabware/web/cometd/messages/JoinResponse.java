package collabware.web.cometd.messages;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Participant;
import collabware.web.shared.JsonProtocolConstants;

public class JoinResponse implements Response {
	
	private final JSONObject payload;
	private final ComplexOperation initSequence;
	private final Participant participant;
	private final List<Participant> participants;
	
	public JoinResponse(ComplexOperation initSequence, Participant localParticipant, List<Participant> participants) {
		this.initSequence = initSequence;
		this.participant = localParticipant;
		this.participants = participants;
		this.payload = createSuccessPayload();
	}

	private JSONObject createSuccessPayload() {
		try {
			JSONObject payload = new JSONObject();
			payload.put(JsonProtocolConstants.SUCCESSFUL, true);
			payload.put(JsonProtocolConstants.INIT_SEQUENCE, Jsonizer.serialize(initSequence));
			payload.put(JsonProtocolConstants.LOCAL_PARTICIPANT, Jsonizer.serialize(participant));
			payload.put(JsonProtocolConstants.ALL_PARTICIPANT, Jsonizer.serialize(participants));
			return payload;
		} catch (JSONException shouldNotHappen) {
			throw new RuntimeException(shouldNotHappen);
		}
	}

	public ComplexOperation getInitSequence() {
		return initSequence;
	}

	public JSONObject getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return payload.toString();
	}
}