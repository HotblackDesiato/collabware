package collabware.model.internal.graph.exception;


public class CannotRemoveRootNodeException extends NodeRemovalException {

	private static final long serialVersionUID = 2826465198019595024L;

	public CannotRemoveRootNodeException() {
		super("Cannot remove the graph's root node.");
	}

}
