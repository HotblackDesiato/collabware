package collabware.api.operations.context;


/**
 * The difference between two {@link Context}s.
 */
public interface ContextDifference extends OperationsSet {

	/**
	 * @return the number of operation in the difference.
	 */
	int size();

	/**
	 * Materializes the context difference.
	 * @param backedOperationsSet
	 * @return the materialized context difference.
	 */
	BackedContextDifference backedBy(DocumentState backedOperationsSet);

}
