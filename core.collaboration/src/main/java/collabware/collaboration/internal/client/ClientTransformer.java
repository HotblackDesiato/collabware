package collabware.collaboration.internal.client;

import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.DocumentState;
import collabware.api.transform.CollisionException;
import collabware.transformer.PrecedenceRule;
import collabware.transformer.TransformationProvider;
import collabware.transformer.Transformer;


class ClientTransformer implements Transformer {

	private final Transformer transformer;

	public static class ClientPrecedenceRule implements PrecedenceRule {

		@Override
		public boolean hasPrecedence(ContextualizedComplexOperation first, ContextualizedComplexOperation second) {
			return  first.getContext().getClientNumber() > second.getContext().getClientNumber();
		}

	}

	public ClientTransformer(TransformationProvider transformationProvider) {
		transformer = transformationProvider.createTransformer(new ClientPrecedenceRule());
	}
	
	@Override
	public ContextualizedComplexOperation transform(ContextualizedComplexOperation op, DocumentState state) throws CollisionException {
		return transformer.transform(op, state);
	}

}
