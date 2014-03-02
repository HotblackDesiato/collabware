package collabware.model.change;

import collabware.api.document.change.Change;


public interface ReferenceSet extends Change {

	public abstract String getNewTargetId();

	public abstract String getOldTargetId();

	public abstract String getReferenceName();

	public abstract String getNodeId();

}
