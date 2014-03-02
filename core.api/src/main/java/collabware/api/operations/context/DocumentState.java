package collabware.api.operations.context;

import collabware.api.document.Document;

/**
 * The state of a {@link Document} is the set of all {@link ContextualizedOperation}s that have been applied to it. 
 *
 */
public interface DocumentState extends OperationsSet {

	/**
	 * 
	 * @param clientNumber
	 * @return the context for client {@code clientNumber} representing the current document state.
	 */
	Context asContextForClient(int clientNumber);
	
	/**
	 * @param context
	 * @return the set of operations that are included in {@code this} but not in {@code context}.
	 */
	BackedContextDifference minus(Context context);
	
	/**
	 * 
	 * @return a set of exactly the same operations.
	 */
	BackedOperationsSet copy();

}
