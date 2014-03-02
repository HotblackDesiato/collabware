package collabware.model.internal.graph.exception;

import collabware.model.graph.ModifyableNode;

public class ResidualAttributeException extends NodeRemovalException {

	private static final long serialVersionUID = -4184672170793170640L;

	public ResidualAttributeException(ModifyableNode toBeRemoved) {
		super("Cannot remove node "+toBeRemoved.getId()+" as it still holds attributes "+toBeRemoved.getAttributes().getAll()+".");
	}
}
