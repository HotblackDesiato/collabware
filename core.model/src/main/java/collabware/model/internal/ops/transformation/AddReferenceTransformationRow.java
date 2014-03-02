package collabware.model.internal.ops.transformation;

import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;

public class AddReferenceTransformationRow extends NaryReferenceTransformationRow<AddReferenceOperation> {

	public AddReferenceTransformationRow(AddReferenceOperation operation) {
		super(operation);
	}
	

	@Override
	protected PrimitiveOperation transformAgainst(RemoveNodeOperation remNode) throws CollisionException {
		if (affectSameNode(getOperation(), remNode) || getOperation().getTargetId().equals(remNode.getNodeId())) {
			throw newCollisionException();
		} else {
			return getOperation();
		}
	}


	@Override
	protected PrimitiveOperation transformAgainst(RemoveReferenceOperation against) {
		if (affectSameNode(getOperation(), against) && affectSameReference(getOperation(), against)) {
			if (getOperation().getPosition() > against.getPosition()) {
				return newAddReferenceOperation(getOperation().getPosition() - 1);
			} else {
				return getOperation();				
			}
		} else {
			return getOperation();				
		}
	}

	@Override
	protected PrimitiveOperation transformAgainst(AddReferenceOperation against) throws CollisionException {
		if (this.affectSameNode(getOperation(), against) && affectSameReference(getOperation(), against)) {
			if (getOperation().getPosition() == against.getPosition()) { 
				if(affectsSameTraget(getOperation(), against)) {
					return nop();							
				} else if (idsAreAlphabeticallyBefore(getOperation(), against)) {
					return getOperation();
				} else {
					return newAddReferenceOperation(getOperation().getPosition()+1);
				}
			} else if (getOperation().getPosition() != against.getPosition() && affectsSameTraget(getOperation(), against)) {
				throw newCollisionException();
			} else if (getOperation().getPosition() > against.getPosition()) {
				return newAddReferenceOperation(getOperation().getPosition() + 1);					
			} else {
				return getOperation();
			}
		} else {
			return getOperation();
		}
	}

	private boolean idsAreAlphabeticallyBefore(AddReferenceOperation operation,	AddReferenceOperation against) {
		return operation.getTargetId().compareTo(against.getTargetId()) > 0;
	}

	private boolean affectsSameTraget(AddReferenceOperation operation, AddReferenceOperation against) {
		return operation.getTargetId().equals(against.getTargetId());
	}

	private PrimitiveOperation newAddReferenceOperation(int newPosition) {
		return new AddReferenceOperation(getOperation().getNodeId(), getOperation().getReferenceName(), newPosition, getOperation().getTargetId());
	}

}