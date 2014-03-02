package collabware.web.cometd.messages;

import org.json.JSONException;
import org.json.JSONObject;

import collabware.web.shared.JsonProtocolConstants;

public class ChangeResponse implements Response {

	private final int clientSequenceNumber;
	private final int serverSequenceNumber;
	private final JSONObject payload;

	public ChangeResponse(int clientSequenceNumber, int serverSequenceNumber) {
		this.clientSequenceNumber = clientSequenceNumber;
		this.serverSequenceNumber = serverSequenceNumber;
		this.payload = createPayload(clientSequenceNumber, serverSequenceNumber);
	}

	private JSONObject createPayload(int clientSequenceNumber, int serverSequenceNumber) {
		try {
			JSONObject payload = new JSONObject();
			payload.put(JsonProtocolConstants.CLIENT_SEQUENCE_NUMBER, clientSequenceNumber);
			payload.put(JsonProtocolConstants.SERVER_SEQUENCE_NUMBER, serverSequenceNumber);
			return payload;
		} catch (JSONException shouldNotHappen) {
			throw new RuntimeException(shouldNotHappen);
		}
	}

	public JSONObject getPayload() {
		return payload;
	}
	
	public int getServerSequenceNumber() {
		return serverSequenceNumber;
	}

	public int getClientSequenceNumber() {
		return clientSequenceNumber;
	}

}
