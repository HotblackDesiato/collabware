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


class AddNodeTransformationRow extends TransformationMatrixRow<AddNodeOperation> {

	public AddNodeTransformationRow(AddNodeOperation addNode) {
		super(addNode);
	}
	
	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) {
		if (affectSameNode(getAddNode(), otherAddNode)) {
			return nop();
		} else {
			return getOperation();
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode)	throws CollisionException {
		if (affectSameNode(getAddNode(), remNode)) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException {
		return getOperation();
	}
	
	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setAttr) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(NoOperation noOp) {
		return getOperation();
	}

	AddNodeOperation getAddNode() {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveReferenceOperation against2) {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddReferenceOperation against2) {
		return getOperation();
	}

}
