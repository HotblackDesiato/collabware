package collabware.model.internal.graph.changes;

import collabware.model.change.NodeAdded;

public class NodeAddedChangeImpl implements NodeAdded {

	private final String nodeId;

	public NodeAddedChangeImpl(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNodeId() == null) ? 0 : getNodeId().hashCode());
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
		NodeAdded other = (NodeAdded) obj;
		if (getNodeId() == null) {
			if (other.getNodeId() != null)
				return false;
		} else if (!getNodeId().equals(other.getNodeId()))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see collabware.model.internal.graph.changes.NodeAdded#getNodeId()
	 */
	@Override
	public String getNodeId() {
		return nodeId;
	}

}
