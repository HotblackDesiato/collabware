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

class SetAttributeTransformationRow extends TransformationMatrixRow<SetAttributeOperation> {

	public SetAttributeTransformationRow(SetAttributeOperation operation) {
		super(operation);
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) throws CollisionException {
		return getOperation();
	}

	private SetAttributeOperation getSetAttr() {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode) throws CollisionException {
		if (affectSameNode(getSetAttr(), remNode)) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation otherSetAttr) throws CollisionException {
		if (affectSameNode(getSetAttr(), otherSetAttr) && affectSameAttributeAs(otherSetAttr)) { 
			if (setSameValueAs(otherSetAttr)) {
				return nop();
			} else {
				throw newCollisionException();
			}
		} else {
			return getOperation();
		}
	}

	private boolean setSameValueAs(SetAttributeOperation otherSetAttr) {
		return (getSetAttr().getNewValue() == null && otherSetAttr.getNewValue() == null) || 
			   (getSetAttr().getNewValue() != null && getSetAttr().getNewValue().equals(otherSetAttr.getNewValue()));
	}

	private boolean affectSameAttributeAs(SetAttributeOperation otherSetAttr) {
		return getSetAttr().getAttributeName().equals(otherSetAttr.getAttributeName());
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setAttr) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(NoOperation against2) {
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
