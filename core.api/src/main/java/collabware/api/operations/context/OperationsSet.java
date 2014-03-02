package collabware.api.operations.context;


/**
 * A set of {@link ContextualizedOperation}s.
 */
public interface OperationsSet {

	/**
	 * Checks whether a {@link ContextualizedOperation} is contained in this set.
	 * @param o
	 * @return true if and only if o is contained.
	 */
	public abstract boolean contains(ContextualizedOperation o);
	
	/**
	 * 
	 * @param o
	 * @return true if and only if o is a OperationSet and contains exactly the same Operations as this.
	 */
	public boolean equals(Object o);

}