package collabware.api.operations.context;

import java.util.List;

import collabware.api.operations.ComplexOperation;
import collabware.api.operations.ComplexOperationImpl;
import collabware.api.operations.PrimitiveOperation;

class ContextualizerImpl implements collabware.api.operations.context.Contextualizer {


	@Override
	public ContextualizedComplexOperation createComplexOperation(
			Context context, ComplexOperation transformed, ContextualizedComplexOperation original) {
		return new ContextualizedComplexOperationImpl(context, transformed, original);
	}

	@Override
	public ContextualizedComplexOperation createComplexOperation(
			Context context, String description, List<PrimitiveOperation> primitives) {
		ComplexOperation complex = new ComplexOperationImpl(description, primitives);
		return new ContextualizedComplexOperationImpl(context, complex);
	}

	@Override
	public ContextualizedComplexOperation createComplexOperation(Context context, ComplexOperation op) {
		return new ContextualizedComplexOperationImpl(context, op);
	}

}
