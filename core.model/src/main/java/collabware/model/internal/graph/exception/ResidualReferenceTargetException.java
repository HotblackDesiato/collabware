package collabware.model.internal.graph.exception;

import collabware.model.graph.ModifyableNode;

public class ResidualReferenceTargetException extends NodeRemovalException {

	private static final long serialVersionUID = 6460133909707825518L;

	public ResidualReferenceTargetException(ModifyableNode toBeRemoved) {
		super("Cannot remove node "+toBeRemoved.getId()+" as it still is target of unary refrences "+toBeRemoved.getNaryReferences().getAll()+".");
	}
}
