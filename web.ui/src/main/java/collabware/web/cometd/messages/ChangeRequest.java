package collabware.web.cometd.messages;

import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collabware.api.document.PrimitiveOperationDeserializer;
import collabware.api.operations.ComplexOperation;
import collabware.web.cometd.utils.ServerJsonProvider;
import collabware.web.shared.JsonProtocolConstants;
import collabware.web.shared.JsonizationException;
import collabware.web.shared.OperationDejsonizer;

public class ChangeRequest implements Request {
	
	private final ComplexOperation operation;
	private final int serverSequenceNumber;
	private final String requesterId;
	private final String collaborationId;
	private final int clientSequenceNumber;

	public ChangeRequest(ServerSession remote, Mutable message, PrimitiveOperationDeserializer operationsProvider) throws JSONException, JsonizationException {
		JSONObject payload = extractPayload(message);
		this.operation = extractOperationFrom(payload, operationsProvider);
		this.serverSequenceNumber = extractServerSequenceNumberFrom(payload);
		this.requesterId = remote.getId();
		this.collaborationId = message.getChannelId().getSegment(2);
		this.clientSequenceNumber = extractClientSequenceNumberFrom(payload);
	}

	private int extractClientSequenceNumberFrom(JSONObject payload) throws JSONException {
		return payload.getInt(JsonProtocolConstants.CLIENT_SEQUENCE_NUMBER);
	}

	private int extractServerSequenceNumberFrom(JSONObject payload) throws JSONException {
		return payload.getInt(JsonProtocolConstants.SERVER_SEQUENCE_NUMBER);
	}

	private ComplexOperation extractOperationFrom(JSONObject payload, PrimitiveOperationDeserializer operationsProvider) throws JSONException, JsonizationException {
		JSONArray operations = payload.getJSONArray(JsonProtocolConstants.OPERATION);
		OperationDejsonizer<JSONObject, JSONArray> dejsonizer = new OperationDejsonizer<JSONObject, JSONArray>(operationsProvider, new ServerJsonProvider());
		return (ComplexOperation) dejsonizer.dejsonize(operations.getJSONObject(0));
	}

	private JSONObject extractPayload(Mutable message) throws JSONException {
		JSONObject json = new JSONObject(message.getJSON());
		return new JSONObject(json.getString(JsonProtocolConstants.PAYLOAD));
	}
	
	public String getRequesterId() {
		return requesterId;
	}

	public int getServerSequenceNumber() {
		return serverSequenceNumber;
	}

	public ComplexOperation getOperation() {
		return operation;
	}

	public String getCollaborationId() {
		return collaborationId;
	}

	public int getClientSequenceNumber() {
		return clientSequenceNumber;
	}

}
