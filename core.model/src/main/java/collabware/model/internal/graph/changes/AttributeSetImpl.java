package collabware.model.internal.graph.changes;

import collabware.model.change.AttributeSet;

public class AttributeSetImpl implements AttributeSet {

	private final String nodeId;
	private final String attributeName;
	private final Object oldValue;
	private final Object newValue;

	public AttributeSetImpl(String nodeId, String attrName, Object oldValue, Object newValue) {
		this.nodeId = nodeId;
		this.attributeName = attrName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result
				+ ((newValue == null) ? 0 : newValue.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result
				+ ((oldValue == null) ? 0 : oldValue.hashCode());
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
		AttributeSetImpl other = (AttributeSetImpl) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (newValue == null) {
			if (other.newValue != null)
				return false;
		} else if (!newValue.equals(other.newValue))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (oldValue == null) {
			if (other.oldValue != null)
				return false;
		} else if (!oldValue.equals(other.oldValue))
			return false;
		return true;
	}

}
