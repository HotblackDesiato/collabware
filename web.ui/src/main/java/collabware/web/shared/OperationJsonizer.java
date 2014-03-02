package collabware.web.shared;

import static collabware.model.operations.SerializationConstants.COMPLEX_OPERATION;
import static collabware.model.operations.SerializationConstants.DESCRIPTION;
import static collabware.model.operations.SerializationConstants.OPERATIONS;
import static collabware.model.operations.SerializationConstants.TYPE;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.Operation;
import collabware.api.operations.PrimitiveOperation;

public class OperationJsonizer<OT, AT> {

	private static Logger logger = Logger.getLogger(OperationJsonizer.class.getName());
	private final JsonProvider<OT, AT> jsonProvider;
	
	public OperationJsonizer(JsonProvider<OT,AT> jsonProvider) {
		this.jsonProvider = jsonProvider;
	}

	public OT jsonize(Operation op) {
		try {
			if (op instanceof ComplexOperation)	  return jsonizeComplex((ComplexOperation)op);
			if (op instanceof PrimitiveOperation) return jsonizePrimitive((PrimitiveOperation)op);
			throw new IllegalArgumentException("Cannot serialize operation of type '" + op.getClass().getName() + "'");
		} catch (JsonizationException e) {
			logger.log(Level.SEVERE, "Excpetion while jsonizing", e);
			// this should not happen...
			throw new RuntimeException(e);
		}
	}

	private OT jsonizePrimitive(PrimitiveOperation op) throws JsonizationException {
		OT jsonObject = jsonProvider.newJsonObject();
		for(Entry<String, Object> entry:op.serialize().entrySet()) {
			jsonProvider.put(jsonObject, entry.getKey(), entry.getValue());
		}
		return jsonObject;
	}

	private OT jsonizeComplex(ComplexOperation cop) throws JsonizationException {
		OT jsonObject = jsonProvider.newJsonObject();
		jsonProvider.put(jsonObject, TYPE, COMPLEX_OPERATION);
		jsonProvider.put(jsonObject, DESCRIPTION, cop.getDescription());
		jsonProvider.put(jsonObject, OPERATIONS, jsonizeList(cop.getPrimitiveOperations()));
		return jsonObject;
	}

	private AT jsonizeList(List<? extends PrimitiveOperation> primitiveOperations) throws JsonizationException {
		AT ops = jsonProvider.newJsonArray();
		for (PrimitiveOperation o: primitiveOperations) {
			jsonProvider.push(ops, jsonize(o));
		}
		return ops;
	}

}