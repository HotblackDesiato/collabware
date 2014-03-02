package collabware.model.internal.ops.transformation;

import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

class NoOperationTransformationRow extends TransformationMatrixRow<NoOperation> {

	public NoOperationTransformationRow(NoOperation operation) {
		super(operation);
	}

	@Override
	protected PrimitiveOperation transformAgainst(NoOperation against2) {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) throws CollisionException {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode) throws CollisionException {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setAttr) throws CollisionException {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveReferenceOperation against2) {
		return nop();
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddReferenceOperation against2) {
		return nop();
	}

}
