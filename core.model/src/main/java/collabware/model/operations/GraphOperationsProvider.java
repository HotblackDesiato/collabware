package collabware.model.operations;

import java.util.Map;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public interface GraphOperationsProvider {
		
	<T extends PrimitiveOperation> T createOperation(Class<T> type, Map<String, String> parameter);
	
	AddNodeOperation createAddNodeOperation(String nodeId);
	
	RemoveNodeOperation createRemoveNodeOperation(String nodeId);
	
	SetAttributeOperation createSetAttributeOperation(String nodeId, String attributeName, Object oldValue, Object newValue);
	
	SetUnaryReferenceOperation createSetUnaryReferenceOperation(String nodeId, String referenceName, String oldTargetId, String newTargetId);
	
	NoOperation createNoOperation();
	
	ComplexOperation createComplexOperation(PrimitiveOperation ... operations);

	RemoveReferenceOperation createRemoveReferenceOperation(String nodeId, String refName, int position, String targetId);
	
	AddReferenceOperation createAddReferenceOperation(String nodeId, String refName, int position, String targetId);
	
}
