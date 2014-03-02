package collabware.collaboration.internal;

import collabware.api.operations.context.Context;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.operations.context.ContextualizedComplexOperationImpl;
import collabware.api.operations.context.DocumentState;
import collabware.transformer.Transformer;

public class NoTransformer implements Transformer {

	@Override
	public ContextualizedComplexOperation transform(ContextualizedComplexOperation complex, DocumentState modelState) {
		Context transformedContext = modelState.asContextForClient(complex.getContext().getClientNumber());
		return new ContextualizedComplexOperationImpl(transformedContext, complex.getDecoratedOperation());
	}

}
