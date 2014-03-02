package collabware.api.operations.context;

/**
 * The context of an Operation is the set of all Operations that where applied before.
 * This interface does not suggest that all these operations are actually materialized.
 * In fact, none of the methods below require the set of operation to be actually materialized.
 * If a materialized set is required, {@link BackedOperationsSet} is used. Some properties of a Context
 * are specific to a client, which is represented by a client number {@link Context#getClientNumber()}
 */
public interface Context extends OperationsSet {

	/**
	 * 
	 * @param o the operation to include
	 * @return the Context that also includes {@code o}.
	 * @throws NonConsecutiveContextException if this.equals(o.getContext()) is false.
	 */
	Context including(ContextualizedOperation o) throws NonConsecutiveContextException;

	/**
	 * 
	 * @param o the context to include
	 * @return the Context that also includes {@code o}.
	 * @throws NonConsecutiveContextException if this.equals(o.getContext()) is false.
	 */	
	Context including(Context o) throws NonConsecutiveContextException;

	/** 
	 * 
	 * 
	 * @param superSet
	 * @return true if and only if this context is a subset of {@code superSet}
	 */
	boolean isSubsetOf(OperationsSet superSet);

	/**
	 * 
	 * @param subtrahend
	 * @return the difference between this and {@code subtrahend}.
	 */
	ContextDifference minus(OperationsSet subtrahend);

	/**
	 * 
	 * @return the client number associated with this context.
	 */
	int getClientNumber();

	/**
	 * The next context associated with the client number.
	 * @see Context#getClientNumber()
	 * @return The next context associated with the client number.
	 */
	Context next();

}
