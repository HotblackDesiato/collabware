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

class SetReferenceTransformationRow extends TransformationMatrixRow<SetUnaryReferenceOperation> {

	public SetReferenceTransformationRow(SetUnaryReferenceOperation operation) {
		super(operation);
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation otherAddNode) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode)	throws CollisionException {
		if (affectSameNode(getSetRef(), remNode) || affectsReferenceTarget(remNode)) {
			throw newCollisionException();
		}
		return getOperation();
	}

	private boolean affectsReferenceTarget(RemoveNodeOperation remNode) {
		return remNode.getNodeId().equals(getSetRef().getNewTargetId());
	}

	private SetUnaryReferenceOperation getSetRef() {
		return getOperation();
	}
	
	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation otherSetRef) throws CollisionException {
		if (affectSameNode(getSetRef(), otherSetRef) && affectsSameReferenceAs(otherSetRef)) {
			if (setsSameNewTargetAs(otherSetRef)) {
				return nop();
			} else {
				throw newCollisionException();
			}
		} else {
			return getOperation();
		}
	}

	private boolean affectsSameReferenceAs(SetUnaryReferenceOperation otherSetRef) {
		return getSetRef().getReferenceName().equals(otherSetRef.getReferenceName());
	}

	private boolean setsSameNewTargetAs(SetUnaryReferenceOperation otherSetRef) {
		return  (getSetRef().getNewTargetId() == null && otherSetRef.getNewTargetId() == null) || 
				(getSetRef().getNewTargetId() != null && getSetRef().getNewTargetId().equals(otherSetRef.getNewTargetId()));
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
