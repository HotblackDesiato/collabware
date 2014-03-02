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

class RemoveNodeTransformationRow extends TransformationMatrixRow<RemoveNodeOperation> {

	public RemoveNodeTransformationRow(RemoveNodeOperation operation) {
		super(operation);
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddNodeOperation addNode) throws CollisionException {
		if (affectSameNode(getRemNode(), addNode)) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	private NodeOperation getRemNode() {
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation otherRemNode)	throws CollisionException {
		if (affectSameNode(getRemNode(), otherRemNode)) {
			return nop();
		} else {
			return getOperation();
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetAttributeOperation setAttr) throws CollisionException {
		if (affectSameNode(getRemNode(), setAttr) && !deletesAttributeValue(setAttr)) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	private boolean deletesAttributeValue(SetAttributeOperation setAttr) {
		return setAttr.getNewValue() == null;
	}

	@Override
	protected PrimitiveOperation transformAgainst(SetUnaryReferenceOperation setRef) throws CollisionException {
		if (affectSameNode(getRemNode(), setRef) && !deletesUnaryReference(setRef) || affectsTargetNodeOf(setRef)) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	private boolean affectsTargetNodeOf(SetUnaryReferenceOperation setRef) {
		return getRemNode().getNodeId().equals(setRef.getNewTargetId());
	}

	@Override
	protected PrimitiveOperation transformAgainst(NoOperation noOp) {
		return getOperation();
	}

	private boolean deletesUnaryReference(SetUnaryReferenceOperation setRef) {
		return setRef.getNewTargetId() == null;
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveReferenceOperation against2) throws CollisionException {
		if (affectSameNode(getOperation(), against2) || against2.getTargetId().equals(getOperation().getNodeId())) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddReferenceOperation against2) throws CollisionException {
		if (affectSameNode(getOperation(), against2) || against2.getTargetId().equals(getOperation().getNodeId())) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}
}