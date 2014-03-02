package collabware.transformer.internal;

import collabware.api.operations.context.ContextualizedComplexOperation;

public class TransformationResult {

	private final ContextualizedComplexOperation first;
	private final ContextualizedComplexOperation second;

	public TransformationResult(ContextualizedComplexOperation first,	ContextualizedComplexOperation second) {
		this.first = first;
		this.second = second;
	}

	public ContextualizedComplexOperation getFirstTransformed() {
		return first;
	}

	public ContextualizedComplexOperation getSecondTransformed() {
		return second;
	}
}