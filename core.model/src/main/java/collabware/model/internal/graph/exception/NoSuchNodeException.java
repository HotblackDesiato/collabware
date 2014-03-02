package collabware.model.internal.graph.exception;

import collabware.api.document.DocumentException;

public class NoSuchNodeException extends DocumentException {

	public NoSuchNodeException(String id) {
		super("No node with id '"+id+"' exists.");
	}

	private static final long serialVersionUID = -7749755312720877674L;

}
