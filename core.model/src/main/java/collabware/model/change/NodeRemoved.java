package collabware.model.change;

import collabware.api.document.change.Change;

public interface NodeRemoved extends Change {

	String getNodeId();
}