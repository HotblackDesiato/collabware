package collabware.collaboration;

import collabware.api.operations.ComplexOperation;

/**
 * A sequence of operations that can be replayed.
 *
 */
public interface ReplaySequence {
	/**
	 * 
	 * @return The operation to initialize the model before the replay.
	 */
	ComplexOperation getInit();

	/**
	 * 
	 * @param i
	 * @return {@code i}th operation in the sequence.
	 * @throws IndexOutOfBoundsException if {@code i} is out of bounds.
	 */
	ComplexOperation get(int i) throws IndexOutOfBoundsException;
	
	/**
	 * 
	 * @return the length of the sequence.
	 */
	int length();

}
