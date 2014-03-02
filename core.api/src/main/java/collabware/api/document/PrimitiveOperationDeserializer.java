package collabware.api.document;

import java.util.Map;

import collabware.api.operations.PrimitiveOperation;

/**
 * Deserializes a PrimitiveOperation. 
 *
 */
public interface PrimitiveOperationDeserializer {

	/**
	 * Deserializes a {@link PrimitiveOperation} from a set of key value pairs. 
	 * The implementation of this operation must match {@link PrimitiveOperation#serialize()} such that:
	 * <pre>{@code 
	 * PrimitiveOperation op1, op2;
	 * op2 = deserializeOperation(op1.serialize());
	 * op1.equals(op2): // true for all op1, op2.
	 * }</pre>
	 * @param map
	 * @return the deserialized primitive operation.
	 */
	PrimitiveOperation deserializeOperation(Map<String, Object> map);

}