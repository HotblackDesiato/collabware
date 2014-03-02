package collabware.model.internal.graph.exception;

import collabware.api.document.DocumentException;
import collabware.model.graph.Node;

public class NodeRemovedException extends DocumentException {

	private static final long serialVersionUID = -6115655778830632741L;

	public NodeRemovedException(Node node) {
		super("Node "+node.getId()+" cannot be modified as it has been removed from the graph.");
	}

}
