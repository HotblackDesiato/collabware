package collabware.web.cometd.messages;

import org.json.JSONObject;

import collabware.collaboration.Participant;

public class ParticipantAddedResponse implements Response {

	private JSONObject payload;

	public ParticipantAddedResponse(Participant participant) {
		payload = Jsonizer.serialize(participant);
	}

	@Override
	public JSONObject getPayload() {
		return payload;
	}

}
