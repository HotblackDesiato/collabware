package collabware.web.client.js;

import java.util.ArrayList;
import java.util.List;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.api.document.change.ComplexChangeEnded;

public class BufferedListener implements ChangeListener {

	private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	private final List<Change> bufferedChanges = new ArrayList<Change>();
	
	public void notifyChange(Change change) {
		log("buffering change: " + change.getClass().getName());
		bufferedChanges.add(change);
		if (change instanceof ComplexChangeEnded) {
			notifyAllListeners();
		}
	}

	private void notifyAllListeners() {
		log("Notifying all listeners");
		for(Change c: bufferedChanges) {
			for (ChangeListener l: listeners) {
				l.notifyChange(c);
			}
		}
		bufferedChanges.clear();
	}

	private native void log(String string) /*-{
		console.log(string);
	}-*/;

	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
}
