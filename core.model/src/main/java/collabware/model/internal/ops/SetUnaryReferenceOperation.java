package collabware.model.internal.ops;

import java.util.Map;

import collabware.api.operations.OperationApplicationException;
import collabware.model.graph.ModifyableGraph;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.exception.NoSuchNodeException;
import static collabware.model.operations.SerializationConstants.*;

public class SetUnaryReferenceOperation extends NodeOperation  {

	private final String referenceName;
	private final String oldTargetId;
	private final String newTargetId;

	public SetUnaryReferenceOperation(String nodeId, String referenceName, String oldTargetId, String newTargetId) {
		super(nodeId);
		this.referenceName = referenceName;
		this.oldTargetId = oldTargetId;
		this.newTargetId = newTargetId;
	}

	public SetUnaryReferenceOperation(Map<String, Object> map) {
		super(map);
		this.referenceName = (String) map.get(REFERENCE_NAME);
		this.oldTargetId   = (String) map.get(OLD_TARGET_ID);
		this.newTargetId   = (String) map.get(NEW_TARGET_ID);
	}

	protected void apply(ModifyableGraph graph) throws OperationApplicationException {
		try {
			ModifyableNode sourceNode = getSourceNode(graph);
			assertCurrentTargetNodeMatchesOldTargetNode(sourceNode); 
			setUnaryReference(graph, sourceNode);
		} catch (NoSuchNodeException e) {
			throw new OperationApplicationException(this, e);
		} 
	}

	private void setUnaryReference(ModifyableGraph graph, ModifyableNode sourceNode) {
		if (getNewTargetId() != null) {
			ModifyableNode targetNode = graph.getNode(getNewTargetId());
			sourceNode.getUnaryReferences().set(referenceName, targetNode);
		} else {
			sourceNode.getUnaryReferences().set(referenceName, null);
		}
	}

	private ModifyableNode getSourceNode(ModifyableGraph graph) {
		return graph.getNode(getNodeId());
	}

	private void assertCurrentTargetNodeMatchesOldTargetNode(ModifyableNode sourceNode) throws OperationApplicationException {
		ModifyableNode currentTargetNode = getCurrentTargetNode(sourceNode);
		if (!oldAndCurrentTargetAreNull(currentTargetNode) && !oldTargetEquals(currentTargetNode)) {
			throw new UnaryReferenceTargetMismatch(this, currentTargetNode);
		}
	}

	private boolean oldTargetEquals(ModifyableNode currentTargetNode) {
		return (currentTargetNode != null && currentTargetNode.getId().equals(oldTargetId));
	}

	private boolean oldAndCurrentTargetAreNull(ModifyableNode currentTargetNode) {
		return (currentTargetNode == null && oldTargetId == null);
	}

	private ModifyableNode getCurrentTargetNode(ModifyableNode sourceNode) {
		return sourceNode.getUnaryReferences().get(referenceName);
	}

	public SetUnaryReferenceOperation inverse() {
		return new SetUnaryReferenceOperation(getNodeId(), referenceName, newTargetId, oldTargetId);
	}

	public String getReferenceName() {
		return referenceName;
	}

	public String getOldTargetId() {
		return oldTargetId;
	}

	public String getNewTargetId() {
		return newTargetId;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(TYPE, SET_REFERENCE_OPERATION);
		m.put(REFERENCE_NAME, this.referenceName);
		m.put(OLD_TARGET_ID, this.oldTargetId);
		m.put(NEW_TARGET_ID, this.newTargetId);
		return m;
	}

	@Override
	public String toString() {
		return "setRef(nodeId='"+getNodeId()+"', referenceName='"+getReferenceName()+"', oldTargetId='"+oldTargetId+"', newTargetId='"+newTargetId+"')";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((newTargetId == null) ? 0 : newTargetId.hashCode());
		result = prime * result
				+ ((oldTargetId == null) ? 0 : oldTargetId.hashCode());
		result = prime * result
				+ ((referenceName == null) ? 0 : referenceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetUnaryReferenceOperation other = (SetUnaryReferenceOperation) obj;
		if (newTargetId == null) {
			if (other.newTargetId != null)
				return false;
		} else if (!newTargetId.equals(other.newTargetId))
			return false;
		if (oldTargetId == null) {
			if (other.oldTargetId != null)
				return false;
		} else if (!oldTargetId.equals(other.oldTargetId))
			return false;
		if (referenceName == null) {
			if (other.referenceName != null)
				return false;
		} else if (!referenceName.equals(other.referenceName))
			return false;
		return true;
	}
	
}
