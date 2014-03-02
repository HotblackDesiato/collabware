package collabware.api.operations.context;

import collabware.api.operations.Operation;

public interface ContextualizedOperation extends Operation, OperationContextDecorator {
	ContextualizedOperation getOriginal();
}
