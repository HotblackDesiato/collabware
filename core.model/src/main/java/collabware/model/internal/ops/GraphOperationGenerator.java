package collabware.model.internal.ops;

import collabware.api.document.change.Change;
import collabware.api.document.change.ComplexChangeEnded;
import collabware.api.operations.OperationGenerator;
import collabware.api.operations.PrimitiveOperation;
import collabware.model.change.AttributeSet;
import collabware.model.change.NodeAdded;
import collabware.model.change.NodeRemoved;
import collabware.model.change.ReferenceAdded;
import collabware.model.change.ReferenceRemoved;
import collabware.model.change.ReferenceSet;

public class GraphOperationGenerator extends OperationGenerator {
	
	@Override
	public void notifyChange(Change change) {
		if (change instanceof NodeAdded) {
			NodeAdded nodeAdded = (NodeAdded) change;
			addGeneratedOperation(createAddNodeOperation(nodeAdded));
			
		} else if (change instanceof NodeRemoved) {
			NodeRemoved nodeRemoved = (NodeRemoved) change;
			addGeneratedOperation(createRemoveNodeOperation(nodeRemoved));			
		
		} else if (change instanceof ReferenceAdded) {
			ReferenceAdded refAdded = (ReferenceAdded)change;
			addGeneratedOperation(createAddReferenceOperation(refAdded));
		
		} else if (change instanceof ReferenceRemoved) {
			ReferenceRemoved refRem= (ReferenceRemoved)change;
			addGeneratedOperation(createRemoveReferenceOperation(refRem));
		
		} else if (change instanceof ReferenceSet) {
			ReferenceSet setRef = (ReferenceSet)change;
			addGeneratedOperation(createSetUnaryReferenceOperation(setRef));
		} else if (change instanceof AttributeSet) {
			AttributeSet setAttr = (AttributeSet)change;
			addGeneratedOperation(createSetAttributeOperation(setAttr));
		
		} else if (change instanceof ComplexChangeEnded) {
			// nothing to do here.
		} else {
			throw new UnsupportedOperationException("Generating operation for change " + change +" not yet implemented.");
		}
	}

	private PrimitiveOperation createSetUnaryReferenceOperation(ReferenceSet setRef) {
		return new SetUnaryReferenceOperation(setRef.getNodeId(), setRef.getReferenceName(), setRef.getOldTargetId(), setRef.getNewTargetId());
	}

	private PrimitiveOperation createSetAttributeOperation(AttributeSet setAttr) {
		return new SetAttributeOperation(setAttr.getNodeId(), setAttr.getAttributeName(), setAttr.getOldValue(), setAttr.getNewValue());
	}

	private PrimitiveOperation createAddReferenceOperation(ReferenceAdded refAdded) {
		return new AddReferenceOperation(refAdded.getNodeId(), refAdded.getReferenceName(), refAdded.getPosition(), refAdded.getTargetId());
	}
	private PrimitiveOperation createRemoveReferenceOperation(ReferenceRemoved refAdded) {
		return new RemoveReferenceOperation(refAdded.getNodeId(), refAdded.getReferenceName(), refAdded.getPosition(), refAdded.getTargetId());
	}

	private PrimitiveOperation createRemoveNodeOperation(NodeRemoved nodeId) {
		return new RemoveNodeOperation(nodeId.getNodeId());
	}

	private PrimitiveOperation createAddNodeOperation(NodeAdded nodeAdded) {
		return new AddNodeOperation(nodeAdded.getNodeId());
	}



	
}
