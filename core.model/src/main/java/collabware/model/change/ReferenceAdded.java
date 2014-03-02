package collabware.model.change;

import collabware.api.document.change.Change;

public interface ReferenceAdded extends Change {

	public abstract String getNodeId();

	public abstract String getReferenceName();

	public abstract String getTargetId();

	public abstract int getPosition();

}