package collabware.web.shared;

import static collabware.model.operations.SerializationConstants.COMPLEX_OPERATION;
import static collabware.model.operations.SerializationConstants.DESCRIPTION;
import static collabware.model.operations.SerializationConstants.NO_OPERATION;
import static collabware.model.operations.SerializationConstants.OPERATIONS;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import collabware.api.document.PrimitiveOperationDeserializer;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.PrimitiveOperation;

public class OperationDejsonizer<OT, AT> {
	
	private final PrimitiveOperationDeserializer opertionsProvider;
	private final JsonProvider<OT, AT> jsonProvider;

	public OperationDejsonizer(PrimitiveOperationDeserializer opertionsProvider, JsonProvider<OT,AT> jsonProvider) {
		this.opertionsProvider = opertionsProvider;
		this.jsonProvider = jsonProvider;
	}

	public Operation dejsonize(OT json) throws JsonizationException {
		if (jsonProvider.getString(json, TYPE).equals(NO_OPERATION)) {
			return NoOperation.NOP;			
		} else if (jsonProvider.getString(json, TYPE).equals(COMPLEX_OPERATION)){
			return dejsonizeComplex(json);
		} else {
			return dejsonizePrimitiveOperation(json);
		}
	}

	private Operation dejsonizePrimitiveOperation(OT json) throws JsonizationException {
		return opertionsProvider.deserializeOperation(asMap(json) );
	}

	private Map<String, Object> asMap(OT json) throws JsonizationException {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key: jsonProvider.getKeys(json)) {
			map.put(key, jsonProvider.getOpt(json, key));
		}
		return map;
	}


	private Operation dejsonizeComplex(OT json) throws JsonizationException {
		
		String description = jsonProvider.getOptString(json, DESCRIPTION, "");
		AT ops = jsonProvider.getArray(json, OPERATIONS);
		List<PrimitiveOperation> primitiveOperations = new ArrayList<PrimitiveOperation>();
		for (int i = 0; i < jsonProvider.length(ops); i++) {
			PrimitiveOperation op = (PrimitiveOperation) dejsonize(jsonProvider.getObject(ops, i));
			primitiveOperations.add(op);
		}
		return new ComplexOperationImpl(description, primitiveOperations);
	}

}