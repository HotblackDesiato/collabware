package collabware.model.operations;

import java.util.Arrays;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.internal.ModelProviderImpl;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public class OperationHelper {

	private OperationHelper() {}

	public static PrimitiveOperation setAttr(String nodeId, String attr, Object oldValue, Object newValue) {
		return new SetAttributeOperation(nodeId, attr, oldValue, newValue);
	}

	public static PrimitiveOperation setRef(String nodeId, String ref, String oldTargetId, String newTargetId) {
		return new SetUnaryReferenceOperation(nodeId, ref, oldTargetId, newTargetId);
	}

	public static PrimitiveOperation addRef(String nodeId, String refName, int position, String targetId) {
		return new AddReferenceOperation(nodeId, refName, position, targetId);
	}

	public static PrimitiveOperation remRef(String nodeId, String refName, int position, String targetId) {
		return new RemoveReferenceOperation(nodeId, refName, position, targetId);
	}

	public static PrimitiveOperation remNode(String nodeId) {
		return new RemoveNodeOperation(nodeId);
	}

	public static PrimitiveOperation addNode(String nodeId) {
		return new AddNodeOperation(nodeId);
	}

	public static ComplexOperation complex(PrimitiveOperation ... ops) {
		return new ComplexOperationImpl("", Arrays.asList(ops));
	}
	
	public static ComplexOperation init(String modelLiteral) {

		return model(modelLiteral).asOperation();
	}

	public static Document model(String modelLiteral) {
		ModelProviderImpl modelProviderImpl = new ModelProviderImpl();
		return modelProviderImpl.createModelFromLiteral("", modelLiteral);
	}
	
}
