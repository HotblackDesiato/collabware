package collabware.collaboration.internal.context;

import collabware.api.operations.context.ContextualizedOperation;

class ContextHelper {

	static VectorBasedContext contextOf(ContextualizedOperation operation) {
		return (VectorBasedContext)operation.getContext();
	}

	static VectorBasedContext selfIncludingContextOf(ContextualizedOperation operation) {
		return (VectorBasedContext)operation.getSelfIncludingContext();
	}

	static int clientNumberOf(ContextualizedOperation operation) {
		return contextOf(operation).getClientNumber();
	}

	static int ownSequenceNumberOf(ContextualizedOperation operation) {
		return selfIncludingContextOf(operation).getSequenceNumbers().get(clientNumberOf(operation));
	}

}
