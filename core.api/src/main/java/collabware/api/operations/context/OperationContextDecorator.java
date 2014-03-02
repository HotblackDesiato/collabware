package collabware.api.operations.context;

import collabware.api.operations.Operation;

/** 
 * Decorates an {@link Operation} with additional bookkeeping information required for the transformation procedures.
 *
 */
public interface OperationContextDecorator {
	/**
	 * 
	 * @return The context of this operation.
	 */
	Context getContext();
	/**
	 * 
	 * @return The original (i.e. non-transformed) operation.
	 */
	Operation getOriginal();
	
	/**
	 * 
	 * @return the context that includes this operation.
	 */
	Context getSelfIncludingContext();
	
	/** 
	 * 
	 * @param state
	 */
	void addTo(BackedOperationsSet state);
		
	/**
	 * @return the decorated plain Operation without any context information.
	 */
	Operation getDecoratedOperation();
}
