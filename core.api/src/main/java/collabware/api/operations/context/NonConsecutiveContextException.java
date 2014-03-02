package collabware.api.operations.context;

/**
 * This exception is thrown if two {@link Context}s are not consecutive. 
 * While {@link Context}s allow for branching and merging, there cannot be any big jumps or missing context.
 * For two {@link Context} to be consecutive, there can only be a difference of one operation between them.
 *
 */
public class NonConsecutiveContextException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2040664390040998805L;

}
