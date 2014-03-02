package collabware.model.internal.graph.changes;

import collabware.model.change.NodeRemoved;

public class NodeRemovedImpl implements NodeRemoved {

	private final String id;

	public NodeRemovedImpl(String id) {
		this.id = id;
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
		NodeRemoved other = (NodeRemoved) obj;
		if (getNodeId() == null) {
			if (other.getNodeId() != null)
				return false;
		} else if (!getNodeId().equals(other.getNodeId()))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see collabware.model.internal.graph.changes.NodeRemoved#getId()
	 */
	@Override
	public String getNodeId() {
		return id;
	}

}
