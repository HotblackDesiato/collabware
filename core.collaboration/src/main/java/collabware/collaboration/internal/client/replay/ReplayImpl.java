package collabware.collaboration.internal.client.replay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import collabware.api.document.Document;
import collabware.api.operations.ComplexOperation;
import collabware.api.operations.OperationApplicationException;
import collabware.api.operations.context.ContextualizedComplexOperation;
import collabware.api.transform.CollisionException;
import collabware.collaboration.client.ClientEndpoint;
import collabware.collaboration.client.replay.Replay;
import collabware.collaboration.client.replay.ReplayControl;
import collabware.collaboration.client.replay.ReplayListener;
import collabware.collaboration.internal.client.ClientState;

public class ReplayImpl implements Replay {
	static final Logger logger = Logger.getLogger("collabware.collaboration.client.ReplayImpl"); 
	
	private final ClientState state;
	private final ClientEndpoint endpoint;

	private boolean enabled = false;
	private boolean inReplayMode = false;

	private List<ContextualizedComplexOperation> updateQueue = new ArrayList<ContextualizedComplexOperation>();
	private ComplexOperation restoreSequence;
	private ReplayControlImpl replayControl;
	
	private ReplayListenerNotifier listeners = new ReplayListenerNotifier();
	
	public ReplayImpl(ClientEndpoint clientEndpoint, ClientState clientState) {
		this.endpoint = clientEndpoint;
		this.state = clientState;
		this.replayControl = new ReplayControlImpl(clientState.getDocument(), listeners);
	}

	public void initReplay(ComplexOperation init, List<ComplexOperation> sequence) {
		inReplayMode = true;
		restoreSequence = restoreSequence(state.getDocument());
		state.getDocument().clear();
		state.getDocument().apply(init);
		replayControl.setReplaySequence(sequence);
		listeners.started();
	}

	private ComplexOperation restoreSequence(Document doc) {
		return doc.asOperation();
	}
	
	private void restoreLiveState() {
		try {
			state.getDocument().clear();
			state.getDocument().apply(this.restoreSequence);
			applyQueuedUpdates();
		} catch (Exception e) {
			throw log(new RuntimeException("Exception while restoring live state: ", e));
		}
	}

	private void applyQueuedUpdates() throws CollisionException, OperationApplicationException {
		for (ContextualizedComplexOperation o: this.updateQueue) {
			state.update(o);
		}
	}

	public void exit() {
		inReplayMode = false;
		replayControl.setReplaySequence(Collections.<ComplexOperation>emptyList());
		restoreLiveState();
		
		listeners.ended();
	}

	public void queueChange(ContextualizedComplexOperation change) {
		updateQueue.add(change);			
	}

	public void enter() {
		if (!enabled) throw new IllegalStateException("Can only replay when disconnected.");
		this.endpoint.fetchReplaySequence();
	}

	public boolean isReplaying() {
		return inReplayMode;
	}
	
	private static <T extends Exception> T log(T e) {
		logger.severe(e.getMessage());
		return e;
	}

	@Override
	public void addListener(ReplayListener listener) {
		listeners.addListener(listener);
	}

	@Override
	public ReplayControl getReplayControl() {
		return replayControl;
	}

	public void enable() {
		this.enabled = true;
	}

}