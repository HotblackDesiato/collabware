package collabware.model.internal.graph.exception;

import collabware.api.document.DocumentException;

public class NodeRemovalException extends DocumentException {

	private static final long serialVersionUID = 7185494468922929075L;

	public NodeRemovalException(String message) {
		super(message);
	}

}