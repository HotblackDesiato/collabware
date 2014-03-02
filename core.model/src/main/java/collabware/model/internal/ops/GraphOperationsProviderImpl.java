package collabware.model.internal.ops;

import static collabware.utils.Asserts.assertNotEmpty;

import java.util.Map;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.operations.GraphOperationsProvider;

public class GraphOperationsProviderImpl implements GraphOperationsProvider {

	GraphOperationsFactory operationsFactory = new GraphOperationsFactory();
	
	public <T extends PrimitiveOperation> T createOperation(Class<T> type, Map<String, String> parameter) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public ComplexOperation createComplexOperation(PrimitiveOperation... operations) {
		assertNotEmpty("operations", operations);
		return operationsFactory.createComplexOperation(operations);
	}

	public AddNodeOperation createAddNodeOperation(String nodeId) {
		return operationsFactory.createAddNodeOperation(nodeId);
	}

	public RemoveNodeOperation createRemoveNodeOperation(String nodeId) {
		return operationsFactory.createRemoveNodeOperation(nodeId);
	}

	public SetAttributeOperation createSetAttributeOperation(String nodeId, String attributeName, Object oldValue,	Object newValue) {
		if (oldValue instanceof Number || newValue instanceof Number)
			return operationsFactory.createSetAttributeOperation(nodeId, attributeName, (Number)oldValue, (Number)newValue);
		else if (oldValue instanceof Boolean || newValue instanceof Boolean)
			return operationsFactory.createSetAttributeOperation(nodeId, attributeName, (Boolean)oldValue, (Boolean)newValue);
		else if (oldValue instanceof String || newValue instanceof String)
			return operationsFactory.createSetAttributeOperation(nodeId, attributeName, (String)oldValue, (String)newValue);
		else 
			throw new IllegalArgumentException("Illegal type for newValue or oldValue. Only Number, String and Boolean are supported.");
	}

	public SetUnaryReferenceOperation createSetUnaryReferenceOperation(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		return operationsFactory.createSetUnaryReferenceOperation(nodeId, referenceName, oldTargetId, newTargetId);
	}

	public NoOperation createNoOperation() {
		return operationsFactory.createNoOperation();
	}

	@Override
	public RemoveReferenceOperation createRemoveReferenceOperation(String nodeId, String refName, int position, String targetId) {
		return operationsFactory.createRemoveReferenceOperation(nodeId, refName, position, targetId);
	}

	@Override
	public AddReferenceOperation createAddReferenceOperation(String nodeId, String refName, int position, String targetId) {
		return operationsFactory.createAddReferenceOperation(nodeId, refName, position, targetId);
	}

}
