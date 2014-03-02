package collabware.collaboration.internal.client.replay;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.replay.ReplayListener;

class ReplayListenerNotifier implements ReplayListener {

	private static Logger LOGGER = Logger.getLogger("collabware.collaboration.client.replay.ReplayListenerNotifier");  
	
	private List<ReplayListener> listeners = new ArrayList<ReplayListener>();

	@Override
	public void replayed(ComplexOperation op) {
		for (ReplayListener l: listeners) {
			try {
				l.replayed(op);
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	private void logException(Exception e) {
		LOGGER.severe("Exception while notifying listener: " + e.getLocalizedMessage());
	}

	@Override
	public void started() {
		for (ReplayListener l: listeners) {
			try {
				l.started();
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	@Override
	public void ended() {
		for (ReplayListener l: listeners) {
			try {
				l.ended();
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	public void addListener(ReplayListener listener) {
		listeners.add(listener);
	}

}
