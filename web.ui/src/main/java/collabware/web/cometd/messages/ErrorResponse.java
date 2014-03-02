package collabware.web.cometd.messages;

import org.json.JSONException;
import org.json.JSONObject;

import collabware.web.shared.JsonProtocolConstants;

public class ErrorResponse implements Response {

	private final JSONObject payload;
	private final String message;
	

	public ErrorResponse(String message) {
		this.message = message;
		this.payload = createJoinFailedMessage();
	}

	public JSONObject getPayload() {
		return payload;
	}
	
	private JSONObject createJoinFailedMessage() {
		try {
			JSONObject payload = new JSONObject();
			payload.put(JsonProtocolConstants.SUCCESSFUL, false);
			payload.put(JsonProtocolConstants.ERROR, message);
			return payload;
		} catch (JSONException doesNotHappen) {
			throw new RuntimeException(doesNotHappen);
		}
	}

}
