package collabware.collaboration.client.replay;

/**
 * Controls the replay model.
 */
public interface Replay {
	
	/**
	 * Adds an listener.
	 * @param listener
	 */
	void addListener(ReplayListener listener);

	/** 
	 * enters replay mode
	 */
	void enter();

	/**
	 * 
	 * @return the ReplayControl to control the replay of operations.
	 */
	ReplayControl getReplayControl();
	
	/**
	 * exits replay mode.
	 */
	void exit();
}
