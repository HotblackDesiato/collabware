package collabware.collaboration.client.replay;

/**
 * Controls the replay of operations performed on a document.
 *
 */
public interface ReplayControl {

	/**
	 * 
	 * @return {@code true} if and only if {@link ReplayControl#previous()} can be called.
	 */
	boolean canPrevious();
	/**
	 * 
	 * @return {@code true} if and only if {@link ReplayControl#next()} can be called.
	 */
	boolean canNext();

	/**
	 * goes to the beginning of the replay sequence.
	 */
	void beginning();
	/**
	 * applies the inverse of the previous operation in the replay sequence.
	 */
	void previous();
	/**
	 * applies the next operation in the replay sequence.
	 */
	void next();
	
	/**
	 * Performs all operation until the end of the replay sequence.
	 */
	void end();

	/**
	 * 
	 * @return the number of operations that can be replayed.
	 */
	int length();
	
	/**
	 * Applies all operation up until a given position of the replay sequence.
	 * @param position
	 */
	void seek(int position);
	
	/** 
	 * 
	 * @return the current position in the replay sequence.
	 */
	int getPosition();
}
