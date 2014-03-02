package collabware.model.internal.ops.transformation;

import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.NaryReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public abstract class NaryReferenceTransformationRow<T extends NaryReferenceOperation> extends TransformationMatrixRow<T> {

	public NaryReferenceTransformationRow(T operation) {
		super(operation);
	}

	protected boolean affectSameReference(NaryReferenceOperation operation, NaryReferenceOperation against) {
		return operation.getReferenceName().equals(against.getReferenceName());
	}

	@Override
	protected PrimitiveOperation transformAgainst(NoOperation noOp) {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setAttr) throws CollisionException {
		return getOperation();
	}

}
