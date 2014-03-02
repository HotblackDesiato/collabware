package collabware.model.internal.graph.changes;

import collabware.model.change.ReferenceAdded;

public class ReferenceAddedImpl implements ReferenceAdded {

	private final String nodeId;
	private final String referenceName;
	private final String targetId;
	private final int position;

	public ReferenceAddedImpl(String nodeId, String referenceName, String targetId, int position) {
		this.nodeId = nodeId;
		this.referenceName = referenceName;
		this.targetId = targetId;
		this.position = position;
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
	public String getTargetId() {
		return targetId;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + position;
		result = prime * result
				+ ((referenceName == null) ? 0 : referenceName.hashCode());
		result = prime * result
				+ ((targetId == null) ? 0 : targetId.hashCode());
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
		ReferenceAddedImpl other = (ReferenceAddedImpl) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (position != other.position)
			return false;
		if (referenceName == null) {
			if (other.referenceName != null)
				return false;
		} else if (!referenceName.equals(other.referenceName))
			return false;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		return true;
	}

}
