package collabware.model.internal.graph.changes;

import collabware.model.change.ReferenceSet;


public class ReferenceSetImpl implements ReferenceSet {

	private final String nodeId;
	private final String referenceName;
	private final String oldTargetId;
	private final String newTargetId;

	public ReferenceSetImpl(String nodeId, String ref, String oldTargetId, String newTargetId) {
		this.nodeId = nodeId;
		this.referenceName = ref;
		this.oldTargetId = oldTargetId;
		this.newTargetId = newTargetId;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String getReferenceName() {
		return referenceName;
	}

	@Override
	public String getOldTargetId() {
		return oldTargetId;
	}

	@Override
	public String getNewTargetId() {
		return newTargetId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((newTargetId == null) ? 0 : newTargetId.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result
				+ ((oldTargetId == null) ? 0 : oldTargetId.hashCode());
		result = prime * result + ((referenceName == null) ? 0 : referenceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceSetImpl other = (ReferenceSetImpl) obj;
		if (newTargetId == null) {
			if (other.newTargetId != null)
				return false;
		} else if (!newTargetId.equals(other.newTargetId))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
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
