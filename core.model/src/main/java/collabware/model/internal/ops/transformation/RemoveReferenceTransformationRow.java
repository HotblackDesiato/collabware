package collabware.model.internal.ops.transformation;

import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;

public class RemoveReferenceTransformationRow extends NaryReferenceTransformationRow<RemoveReferenceOperation> {

	public RemoveReferenceTransformationRow(RemoveReferenceOperation operation) {
		super(operation);
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode) throws CollisionException {
		if (affectSameNode(getOperation(), remNode) || getOperation().getTargetId().equals(remNode.getNodeId())) {
			throw newCollisionException();
		}
		return getOperation();
	}

	@Override
	protected PrimitiveOperation transformAgainst(RemoveReferenceOperation against2) throws CollisionException {
		if (affectSameNode(getOperation(), against2) && affectSameReference(getOperation(), against2)) {
			if (getOperation().getPosition() > against2.getPosition()) {
				return newRemoveReference(getOperation().getPosition() - 1);
			} else if (getOperation().getPosition() == against2.getPosition()) {
				throw newCollisionException();
			} else {
				return getOperation();				
			}
		} else {
			return getOperation();			
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddReferenceOperation against2) {
		if (affectSameNode(getOperation(), against2) && affectSameReference(getOperation(), against2)) {
			if (getOperation().getPosition() >= against2.getPosition()) {
				return newRemoveReference(getOperation().getPosition() + 1);
			} else {
				return getOperation();
			}
		} else {
			return getOperation();
		}
	}

	private PrimitiveOperation newRemoveReference(int newPosition) {
		return new RemoveReferenceOperation(getOperation().getNodeId(), getOperation().getReferenceName(), newPosition, getOperation().getTargetId());
	}

}
