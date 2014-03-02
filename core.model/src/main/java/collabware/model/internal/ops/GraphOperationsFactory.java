package collabware.model.internal.ops;

import java.util.Arrays;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;

public class GraphOperationsFactory {

	public ComplexOperation createComplexOperation(PrimitiveOperation... operations) {
		return new ComplexOperationImpl("", Arrays.asList(operations));
	}

	public AddNodeOperation createAddNodeOperation(String nodeId) {
		return new AddNodeOperation(nodeId);
	}

	public RemoveNodeOperation createRemoveNodeOperation(String nodeId) {
		return new RemoveNodeOperation(nodeId);
	}

	public SetAttributeOperation createSetAttributeOperation(String nodeId, String attributeName, String oldValue,	String newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	public SetAttributeOperation createSetAttributeOperation(String nodeId,	String attributeName, Number oldValue, Number newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}
	
	public SetAttributeOperation createSetAttributeOperation(String nodeId, String attributeName, Boolean oldValue, Boolean newValue) {
		return new SetAttributeOperation(nodeId, attributeName, oldValue, newValue);
	}

	public SetUnaryReferenceOperation createSetUnaryReferenceOperation(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return new SetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}

	public NoOperation createNoOperation() {
		return (NoOperation) NoOperation.NOP;
	}

	public RemoveReferenceOperation createRemoveReferenceOperation(String nodeId, String refName, int position, String targetId) {
		return new RemoveReferenceOperation(nodeId, refName, position, targetId);
	}

	public AddReferenceOperation createAddReferenceOperation(String nodeId,	String refName, int position, String targetId) {
		return new AddReferenceOperation(nodeId, refName, position, targetId);
	}
}
