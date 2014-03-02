package collabware.model.internal.graph.exception;

import collabware.model.graph.ModifyableNode;

public class ResidualReferenceException extends NodeRemovalException {

	private static final long serialVersionUID = 6460133909707825518L;

	public ResidualReferenceException(ModifyableNode toBeRemoved) {
		super("Cannot remove node "+toBeRemoved.getId()+" as it still holds unary refrences "+toBeRemoved.getNaryReferences().getAll()+".");
	}
}
