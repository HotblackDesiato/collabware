package collabware.model.internal.ops;

import static collabware.model.operations.SerializationConstants.POSITION;
import static collabware.model.operations.SerializationConstants.REFERENCE_NAME;

import java.util.Map;


public abstract class NaryReferenceOperation extends NodeOperation {


	protected final String refName;
	protected final int position;
	protected final String targetId;

	public NaryReferenceOperation(String nodeId, String refName, int position, String targetId) {
		super(nodeId);
		this.refName = refName;
		this.position = position;
		this.targetId = targetId;
	}

	public NaryReferenceOperation(Map<String, Object> map, String targetId) {
		super(map);
		this.refName = (String) map.get(REFERENCE_NAME);
		this.position = ((Number) map.get(POSITION)).intValue();
		this.targetId = targetId;
	}

	public String getReferenceName() {
		return refName;
	}

	public String getTargetId() {
		return targetId;
	}

	public int getPosition() {
		return position;
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> m = super.serialize();
		m.put(REFERENCE_NAME, 	this.refName);
		m.put(POSITION, 		this.position);
		return m;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + position;
		result = prime * result + ((refName == null) ? 0 : refName.hashCode());
		result = prime * result + ((targetId == null) ? 0 : targetId.hashCode());
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
		NaryReferenceOperation other = (NaryReferenceOperation) obj;
		if (position != other.position)
			return false;
		if (refName == null) {
			if (other.refName != null)
				return false;
		} else if (!refName.equals(other.refName))
			return false;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		return true;
	}

}
