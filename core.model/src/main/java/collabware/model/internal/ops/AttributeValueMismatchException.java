package collabware.model.internal.ops;

import collabware.api.operations.OperationApplicationException;

public class AttributeValueMismatchException extends OperationApplicationException {

	public AttributeValueMismatchException(String nodeId, String attributeName,	Object expectedValue, Object currentAttributeValue) {
		super(renderMessage(nodeId, attributeName, expectedValue, currentAttributeValue));
	}

	private static String renderMessage(String nodeId, String attributeName, Object expectedValue, Object currentAttributeValue) {
		return "Attribute value mismatch between operation " +
				"and model concerning node '"+nodeId+"' and attribute '"+attributeName+"'. " +
				"Expected '"+String.valueOf(expectedValue)+"' but was '"+String.valueOf(currentAttributeValue)+"'.";
	}

	private static final long serialVersionUID = -7130226710779704012L;

}
