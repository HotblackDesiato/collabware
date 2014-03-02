package collabware.model.change;

import collabware.api.document.change.Change;


public interface ReferenceRemoved extends Change {

	public abstract int getPosition();

	public abstract String getTargetId();

	public abstract String getReferenceName();

	public abstract String getNodeId();

}
