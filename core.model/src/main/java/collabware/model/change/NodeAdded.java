package collabware.model.change;

import collabware.api.document.change.Change;

public interface NodeAdded extends Change {

	public abstract String getNodeId();

}