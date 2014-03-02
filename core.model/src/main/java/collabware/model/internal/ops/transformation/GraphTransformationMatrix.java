package collabware.model.internal.ops.transformation;

import collabware.api.document.PrimitiveOperationTransformer;
import collabware.api.operations.NoOperation;
import collabware.api.operations.PrimitiveOperation;
import collabware.api.transform.CollisionException;
import collabware.model.internal.ops.AddNodeOperation;
import collabware.model.internal.ops.AddReferenceOperation;
import collabware.model.internal.ops.RemoveNodeOperation;
import collabware.model.internal.ops.RemoveReferenceOperation;
import collabware.model.internal.ops.SetAttributeOperation;
import collabware.model.internal.ops.SetUnaryReferenceOperation;

public class GraphTransformationMatrix implements PrimitiveOperationTransformer {
	
	public TransformationMatrixRow<? extends PrimitiveOperation> transform(PrimitiveOperation operation) {
		if (operation instanceof AddNodeOperation) {	
			return new AddNodeTransformationRow((AddNodeOperation) operation);
		} else if (operation instanceof RemoveNodeOperation) {	
			return new RemoveNodeTransformationRow((RemoveNodeOperation) operation);
		} else if (operation instanceof SetAttributeOperation) {	
			return new SetAttributeTransformationRow((SetAttributeOperation) operation);
		} else if (operation instanceof SetUnaryReferenceOperation) {	
			return new SetReferenceTransformationRow((SetUnaryReferenceOperation) operation);
		} else if (operation instanceof SetUnaryReferenceOperation) {	
			return new SetReferenceTransformationRow((SetUnaryReferenceOperation) operation);
		} else if (operation instanceof AddReferenceOperation) {	
			return new AddReferenceTransformationRow((AddReferenceOperation) operation);
		} else if (operation instanceof RemoveReferenceOperation) {	
			return new RemoveReferenceTransformationRow((RemoveReferenceOperation) operation);
		} else if (operation instanceof NoOperation) {
			return new NoOperationTransformationRow((NoOperation)operation);
		}
		throw new IllegalArgumentException("Unknown operation type '"+operation.getClass().getName()+"'.");
	}

	@Override
	public PrimitiveOperation transform(PrimitiveOperation base, PrimitiveOperation against) throws CollisionException {
		return this.transform(base).against(against);
	}
}
