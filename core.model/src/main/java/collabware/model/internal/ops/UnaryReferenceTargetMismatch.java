package collabware.model.internal.ops;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableNode;

public class UnaryReferenceTargetMismatch extends OperationApplicationException {
	
	public UnaryReferenceTargetMismatch(SetUnaryReferenceOperation operation, ModifyableNode currentTargetNode) {
		super(renderMessage(operation, currentTargetNode));
	}

	private static String renderMessage(SetUnaryReferenceOperation operation, ModifyableNode currentTargetNode) {
		Object currentTargetNodeId;
		if (currentTargetNode == null) {
			currentTargetNodeId = null;
		} else {
			currentTargetNodeId = currentTargetNode.getId();
		}
		return "Unary reference target mismatch between operation " +
				"and model concerning node '"+operation.getNodeId()+"' and reference '"+operation.getReferenceName()+"'. " +
				"Expected '"+operation.getOldTargetId()+"' but was '"+currentTargetNodeId+"'.";
	}

	private static final long serialVersionUID = 1803154802355979184L;

}
