package collabware.model.internal.graph.exception;

import collabware.api.document.DocumentException;

public class DuplicateNodeException extends DocumentException {

	private static final long serialVersionUID = -7856564269475423975L;

	public DuplicateNodeException(String duplicateNodeId) {
		super("Duplicate Node id "+ duplicateNodeId +".");
	}
}
