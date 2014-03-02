package collabware.model.change;

import collabware.api.document.change.Change;


public interface AttributeSet extends Change {

	String getNodeId();

	String getAttributeName();

	Object getOldValue();

	Object getNewValue();

}
