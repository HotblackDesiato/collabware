package collabware.api.document.change;

public interface ChangeListener {
	
	static class NullListener implements ChangeListener {
		@Override
		public void notifyChange(Change change) {
			// do nothing
		}
	}
	
	static final ChangeListener NULL_LISTENER = new NullListener();
	
	void notifyChange(Change change);
}
