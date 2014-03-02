package collabware.web.client.cometd;

import collabware.api.operations.ComplexOperation;
import collabware.model.internal.ModelProviderImpl;
import collabware.web.client.GwtJsonProvider;
import collabware.web.shared.JsonProtocolConstants;
import collabware.web.shared.JsonProvider;
import collabware.web.shared.JsonizationException;
import collabware.web.shared.OperationDejsonizer;
import collabware.web.shared.OperationJsonizer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class UpdateMessage {

	private final ComplexOperation complexOperation;
	private final int clientSequenceNumber;
	private final int serverSequenceNumber;

	public UpdateMessage(ComplexOperation complexOperation,	int clientSequenceNumber, int serverSequenceNumber) {
		this.complexOperation = complexOperation;
		this.clientSequenceNumber = clientSequenceNumber;
		this.serverSequenceNumber = serverSequenceNumber;
	}
	
	public <OT, AT> UpdateMessage(String jsonPayload, JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		OperationDejsonizer<OT, AT> dejasonizer = new OperationDejsonizer<OT,AT>(new ModelProviderImpl(), jsonProvider);
		OT json = jsonProvider.parse(jsonPayload);
		this.complexOperation = (ComplexOperation) dejasonizer.dejsonize(jsonProvider.getObject(json, JsonProtocolConstants.OPERATION));
		this.clientSequenceNumber = jsonProvider.getInt(json, JsonProtocolConstants.CLIENT_SEQUENCE_NUMBER);
		this.serverSequenceNumber = jsonProvider.getInt(json, JsonProtocolConstants.SERVER_SEQUENCE_NUMBER);
	}
	
	public UpdateMessage(String jsonPayload) throws JsonizationException {
		this(jsonPayload, new GwtJsonProvider());
	}

	public String toString() {
		return toString(new GwtJsonProvider());
	}

	public <OT, AT> String toString(JsonProvider<OT, AT> jsonProvider) {
		try {
			OT message = toJson(jsonProvider);
			return message.toString();
		} catch (JsonizationException e) {
			throw new RuntimeException(e);
		}
	}

	private <OT, AT> OT toJson(JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		OT message = jsonProvider.newJsonObject();
		jsonProvider.put(message, JsonProtocolConstants.OPERATION, jsonizeOperations(jsonProvider));
		jsonProvider.put(message, JsonProtocolConstants.SERVER_SEQUENCE_NUMBER, this.serverSequenceNumber);
		jsonProvider.put(message, JsonProtocolConstants.CLIENT_SEQUENCE_NUMBER, this.clientSequenceNumber);
		return message;
	}

	private <AT, OT> AT jsonizeOperations(JsonProvider<OT, AT> jsonProvider) throws JsonizationException {
		OperationJsonizer<OT, AT> jsonizer = new OperationJsonizer<OT, AT>(jsonProvider);
		AT operations = jsonProvider.newJsonArray();
		OT serialized = (OT) jsonizer.jsonize(complexOperation);
		jsonProvider.push(operations, serialized);
		return operations;
	}

	public JavaScriptObject toJson() throws JsonizationException {
		JSONObject serialized = toJson(new GwtJsonProvider());
		return serialized.getJavaScriptObject();
	}

	public Integer getClientSequenceNumber() {
		return clientSequenceNumber;
	}

	public Integer getServerSequenceNumber() {
		return serverSequenceNumber;
	}

	public ComplexOperation getChange() {
		return complexOperation;
	}
}
