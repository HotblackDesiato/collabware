package collabware.api.operations;

import java.util.Map;

/**
 * A primitive, that is indivisible, operation.
 */
public interface PrimitiveOperation extends Operation {
	/**
	 * {@inheritDoc Operation#inverse()}
	 */
	PrimitiveOperation inverse();
	
	/**
	 * Serialized this operation into key value pairs, where the value is a primitive Java type (e.g. String, int, double, boolean, ...)
	 * @return the key value pairs into which this operation has been serialized.
	 */
	Map<String, Object> serialize();
}
