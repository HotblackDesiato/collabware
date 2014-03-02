package collabware.api.operations.context;


/**
 * A context difference that is backed by some actual {@link ContextualizedOperation}s.
 *
 */
public interface BackedContextDifference extends ContextDifference {

	/**
	 * Removes a {@code ContextualizedOperation} which has a sub-context of {@code op}.
	 * @param op
	 * @return the above operation.
	 * @throws NoSuchOperationException
	 */
	ContextualizedOperation selectAndRemoveWithSubContextOf(ContextualizedOperation op) throws NoSuchOperationException;

}
