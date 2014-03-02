package collabware.web.cometd.messages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collabware.api.operations.ComplexOperation;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.JsonProtocolConstants;
import collabware.web.shared.OperationJsonizer;

public class ApplyChangeMessage implements Response {

	private final int clientSequenceNumber;
	private final ComplexOperation change;
	private final int serverSequenceNumber;
	private final JSONObject payload;

	public ApplyChangeMessage(int clientSequenceNumber,	int serverSequenceNumber, ComplexOperation change) {
		this.clientSequenceNumber = clientSequenceNumber;
		this.serverSequenceNumber = serverSequenceNumber;
		this.change = change;
		this.payload = createPayload();
	}

	private JSONObject createPayload() {
		try {
			JSONObject payload = new JSONObject();
			payload.put(JsonProtocolConstants.CLIENT_SEQUENCE_NUMBER, clientSequenceNumber);
			payload.put(JsonProtocolConstants.SERVER_SEQUENCE_NUMBER, serverSequenceNumber);
			payload.put(JsonProtocolConstants.OPERATION, serialize(change));
			return payload;
		} catch (JSONException shouldNotHappen) {
			throw new RuntimeException(shouldNotHappen);
		}
	}

	private static JSONObject serialize(ComplexOperation operation) {
		OperationJsonizer<JSONObject, JSONArray> jsonizer = new OperationJsonizer<JSONObject, JSONArray>(new ServerJsonProvider());
		return jsonizer.jsonize(operation);
	}
	
	public JSONObject getPayload() {
		return payload;
	}

	public int getClientSequenceNumber() {
		return clientSequenceNumber;
	}

	public ComplexOperation getChange() {
		return change;
	}

	public int getServerSequenceNumber() {
		return serverSequenceNumber;
	}

}
