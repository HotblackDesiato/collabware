package collabware.model.internal.ops.transformation;

import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.NodeOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public abstract class TransformationMatrixRow<T extends PrimitiveOperation> {

	private final T operation;
	private PrimitiveOperation against;

	public TransformationMatrixRow(T operation) {
		this.operation = operation;
	}

	public PrimitiveOperation against(PrimitiveOperation against) throws CollisionException {
		this.against = against;
		if (against instanceof AddNodeOperation) {
			return transformAgainst((AddNodeOperation) against);
		} else if (against instanceof RemoveNodeOperation){
			return transformAgainst((RemoveNodeOperation) against);
		} else if (against instanceof SetAttributeOperation){
			return transformAgainst((SetAttributeOperation) against);
		} else if (against instanceof SetUnaryReferenceOperation){
			return transformAgainst((SetUnaryReferenceOperation) against);
		} else if (against instanceof AddReferenceOperation){
			return transformAgainst((AddReferenceOperation) against);
		} else if (against instanceof RemoveReferenceOperation){
			return transformAgainst((RemoveReferenceOperation) against);
		} else if (against instanceof NoOperation){
			return transformAgainst((NoOperation) against);
		} else {
			throw new IllegalArgumentException("Unknown operation type " + against.getClass().getName());
		}
	
	}

	protected boolean affectSameNode(NodeOperation operation, NodeOperation otherAddNode) {
		return operation.getNodeId().equals(otherAddNode.getNodeId());
	}
	
	protected abstract PrimitiveOperation transformAgainst(NoOperation noOp);

	protected abstract PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) throws CollisionException;

	protected abstract PrimitiveOperation transformAgainst(RemoveNodeOperation remNode) throws CollisionException;
	
	protected abstract PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException;
	
	protected abstract PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setAttr) throws CollisionException;

	protected abstract PrimitiveOperation transformAgainst(RemoveReferenceOperation against) throws CollisionException;

	protected abstract PrimitiveOperation transformAgainst(AddReferenceOperation against) throws CollisionException;

	public T getOperation() {
		return operation;
	}

	protected CollisionException newCollisionException() {
		return new CollisionException(against, getOperation());
	}

	protected PrimitiveOperation nop() {
		return NoOperation.NOP;
	}

}